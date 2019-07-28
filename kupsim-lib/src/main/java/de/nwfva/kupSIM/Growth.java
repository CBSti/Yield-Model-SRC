package de.nwfva.kupSIM;

import treegross.base.FunctionInterpreter;

import treegross.base.Stand;

/**
 * Created by cstiehm on 02.03.17.
 *
 * Growth routines for KUPSim
 */
public class Growth {
    private FunctionInterpreter fi;
    
    public Growth() {
        
        fi = new FunctionInterpreter();
    }
    
    public Growth(FunctionInterpreter fi) {
        
        this.fi = fi;
    }

    public void grow(Stand st) {

        st.ageclass += 1;
        if (st.ageclass <= 3) {

            for (int i = 0; i < st.ntrees; i++) {
                if (st.tr[i].out == -1) {
                    st.tr[i].age += 1;
                        st.tr[i].h += fi.getValueForTree(st.tr[i], st.tr[i].sp.spDef.heightIncrementXML);
                        st.tr[i].volumeDeadwood = fi.getValueForTree(st.tr[i], st.tr[i].sp.spDef.heightIncrementXML);

                }
            }
            } else {
                for (int i = 0; i < st.ntrees; i++) {
                    if (st.tr[i].out == -1) {
                        st.tr[i].age += 1;

                        if (st.tr[i].h <= 1.3) {
                            st.tr[i].h += fi.getValueForTree(st.tr[i], st.tr[i].sp.spDef.maximumDensityXML); // incrHSmallRot2LM
                            st.tr[i].volumeDeadwood = fi.getValueForTree(st.tr[i], st.tr[i].sp.spDef.maximumDensityXML);
                        } else {
                            st.tr[i].h += fi.getValueForTree(st.tr[i], st.tr[i].sp.spDef.diameterIncrementXML); // incrHRot2LM
                            st.tr[i].volumeDeadwood =
                                    fi.getValueForTree(st.tr[i], st.tr[i].sp.spDef.diameterIncrementXML); //
                        }

                    }
                }
        }
    }



    public void resprout(Stand st) {

        st.ageclass += 1;

        for (int i = 0; i < st.ntrees; i++) {

            st.tr[i].c66 = 0; // Resprout
            st.tr[i].c66c = 0; // Resprout
            st.tr[i].volumeDeadwood = 0;

            if(st.tr[i].out == -1) {
                st.tr[i].age += 1;
                double survResp = this.fi.getValueForTree(st.tr[i], st.tr[0].sp.spDef.fineRootBiomass); // survRespGLM

                if (Math.random() > survResp) {
                    st.tr[i].out = st.tr[i].age;
                    st.tr[i].h = 0;
                } else {
                    st.tr[i].h =  fi.getValueForTree(st.tr[i], st.tr[i].sp.spDef.decayXML); // survIncrLM
                    st.tr[i].volumeDeadwood = st.tr[i].h; // survIncrLM
                }
        } else {
                st.tr[i].h = 0;
            }
        }
    }
}

