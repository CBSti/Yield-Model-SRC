package de.nwfva.kupSIM;


import treegross.base.FunctionInterpreter;
import treegross.base.Stand;
import treegross.base.Tree;

public class DataCompletion {
      
    private FunctionInterpreter fi;
    
    public DataCompletion() {
        
        fi = new FunctionInterpreter();
    }
    
    public DataCompletion(FunctionInterpreter fi) {
        this.fi = fi;
    }


    
    public void completeData(Stand st) {

        if (st.tr[0].age <= 3) {
            // Estimate missing data for first Rotation
            for (int i = 0; i < st.ntrees; i++) {
                if (st.tr[i].   out == -1) {
                    // c66 contains basal area [m²] based on RCD now
                    st.tr[i].c66 = fi.getValueForTree(st.tr[i],  st.tr[i].sp.spDef.stemVolumeFunctionXML)/10000;
                }
            }
        } else {
            for (int i = 0; i < st.ntrees; i++) {
                if (st.tr[i].out == -1) {
                    if (st.tr[i].h >= 1.3) {
                        //st.tr[i].cw = Math.log(st.tr[i].h);
                        st.tr[i].c66 = fi.getValueForTree(st.tr[i], st.tr[i].sp.spDef.siteindexHeightXML)/10000; // estimates dbh by height

                    }
                }
            }
        }

        for(int i = 0; i < st.ntrees - 1; ++i) {
            for(int j = i + 1; j < st.ntrees; ++j) {
                if (st.tr[i].c66 < st.tr[j].c66) {
                    st.tr[st.ntrees + 1] = st.tr[i];
                    st.tr[i] = st.tr[j];
                    st.tr[j] = st.tr[st.ntrees + 1];
                }
            }
        }

        // Set basal area in larger trees for first and second rank, variable c66c contains BAL now
        st.tr[0].c66c = 0;
        st.tr[1].c66c = st.tr[0].c66;

        // Calculate remaining BAL, if else for tied ranks
        for (int i = 2; i < st.ntrees; i++) {
            if (st.tr[i].c66 == st.tr[i-1].c66) {
                st.tr[i].c66c = st.tr[i-1].c66c;
            } else {
                st.tr[i].c66c = st.tr[i-1].c66c + st.tr[i-1].c66;
            }
        }

        for (int i = 0; i < st.ntrees; i++) {
            st.tr[i].c66c = st.tr[i].c66c * 11111/st.ntrees;
        }


        for(int i = 0; i < st.ntrees - 1; ++i) {
            for(int j = i + 1; j < st.ntrees; ++j) {
                if (Integer.parseInt(st.tr[i].no) < Integer.parseInt(st.tr[j].no)) {
                    st.tr[st.ntrees + 1] = st.tr[i];
                    st.tr[i] = st.tr[j];
                    st.tr[j] = st.tr[st.ntrees + 1];
                }
            }
        }

        Summary sum = new Summary();
        Tree lMean = new Tree();
        lMean.h = sum.getMeanHeight(st);

        // Estimate Mean stand height
        //DiameterDistributionXM function is currently superimposed with mean height estimation after year 1
        if(st.tr[0].age <= 3) {
            st.sp[0].d100 = fi.getValueForTree(lMean, st.tr[0].sp.spDef.siteindexXML);
        } else {
            st.sp[0].d100 = fi.getValueForTree(lMean, st.tr[0].sp.spDef.potentialHeightIncrementXML);
        }


    }
}
