package de.nwfva.kupSIM;

import treegross.base.Stand;


public class Summary {

    private double meanHeight;
    private double sumHeight;
    private double sumMass;
    private double sumBA;
    private double sumSurv;

    public Summary() {
        this.meanHeight = 0;
        this.sumHeight = 0;
        this.sumMass = 0;
        this.sumBA = 0;
        this.sumSurv = 0;
    }

    public double getMeanHeight(Stand st) {
        sumHeight = 0;
        meanHeight = 0;
        sumSurv = 0;
        for (int i = 0; i < st.ntrees; i++) {
            if(st.tr[i].out == -1) {
                sumHeight += st.tr[i].h;
                sumSurv++;
            }
        }
        return this.meanHeight = sumHeight / sumSurv;
    }

    public double getSumSurv(Stand st) {
        sumSurv = 0;
        for (int i = 0; i < st.ntrees; i++) {
           if(st.tr[i].out == -1) {
               sumSurv++;
           }
        }
        return this.sumSurv = sumSurv / st.ntrees * 100;
    }

    public String getSummaryString(Stand st) {
        String summaryString = "<html>N " + st.ntrees +
                " <br>Survival rate: " + Math.rint(this.getSumSurv(st) * 100.0) / 100.0 + " % " +
                " <br>Mean height: " + Math.rint(this.getMeanHeight(st) * 100.0) / 100.0  + " m " +
                //" <br>Basal area: " + Math.rint(this.getSumBA(st) * 100.0) / 100.0 + " m<sup>2</sup> ha\u207B\u00B9 " +
                " <br>Mean annual increment: " + Math.rint(st.sp[0].d100 * 100.0 ) / 100.0 + " t ha\u207B\u00B9 a\u207B\u00B9 </html>";
        return summaryString;
    }


}
