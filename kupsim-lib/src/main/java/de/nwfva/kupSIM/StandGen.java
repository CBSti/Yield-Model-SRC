package de.nwfva.kupSIM;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import treegross.base.*;
import treegross.random.RandomNumber;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StandGen {

    private Stand st = null;
    private FunctionInterpreter fi;
    private Logger logger;
    private String outputFolder;
    private String configDir;
    private String configFile;
    private String modelRegion;


    public StandGen(String outputFolder, String configDir, String configFile, String modelRegion, Logger logger) {
        this.fi = new FunctionInterpreter();
        this.outputFolder = outputFolder;
        this.configDir = configDir;
        this.configFile = configFile;
        this.modelRegion = modelRegion;
        this.logger = logger;

    }

    private String getProgramDir() {

        Path configDirPath = Paths.get(this.configDir);

        File file = configDirPath.toFile();
        return file.getAbsolutePath();
    }

    private String getConfigFile(String clone) {

        String configFile = getConfigFileName(clone);

        Path configDirPath = Paths.get(this.configDir, configFile);

        File file = configDirPath.toFile();
        return file.getAbsolutePath();
    }

    private String getConfigFileName(String clone) {

        return String.format("SRCSim_%s_ModelParam.xml", clone);
    }

    private String getOutputDir() {

        Path outputDirPath = Paths.get(this.outputFolder);

        File file = outputDirPath.toFile();
        return file.getAbsolutePath();
    }

    private String getOutputFile(String filename) {

        Path outputDirPath = Paths.get(this.outputFolder, filename);

        File file = outputDirPath.toFile();
        return file.getAbsolutePath();
    }

    public Stand standGen(String clone, double awc, int doyPlant, double prec, double temp, int bz) {

        logger.debug("Generate Stand. Clone = {}, awc = {}, DoyPlant = {}, prec = {}, temp = {}");

        st = new Stand();
        st.modelRegion = this.modelRegion;
        st.FileXMLSettings = getConfigFileName(clone);

        logger.debug("st.FileXmlSettings: {}", st.FileXMLSettings);
        SpeciesDefMap SDM = new SpeciesDefMap();

        URL url = this.getClass().getClassLoader().getResource(getConfigFileName(clone));
        SDM.readFromURL(url);
        st.debug = false;
        st.setSDM(SDM);

        //st.size = 0.2520025;
        //st.maxStandTrees = 11111;
        st.standname = "src";
        st.riskActive = false;
        st.distanceDependent = true;
        st.random.setRandomType(treegross.random.RandomNumber.PSEUDO);
        st.ingrowthActive = false;
        st.year = 1;
        st.timeStep = 1;
        st.ageclass = 0;
        TreegrossXML2 xmlParser = new TreegrossXML2();


        // Add corner points for the stand
        // TreeGrOSS allows for varying shapes and sizes of stands, this has been fixed here
        // There is no correction for slope here, the slope is taken to be 0

        st.addcornerpoint("POLYGON", 5.0, 2.0, 0.0);

        st.addcornerpoint("ECK1", 0.0, 0.0, 0.0);
        st.addcornerpoint("ECK2", 0.0, 2.0, 0.0);
        st.addcornerpoint("ECK3", 5.0, 2.0, 0.0);
        st.addcornerpoint("ECK4", 5.0, 0.0, 0.0);
        try {
            double xab = 1.8;
            double yab = 0.5;
            double xp = xab / 2.0;
            double yp;
            int mm = 0;
            do {
                yp = yab / 2.0; // do not change
                do {
                    mm = mm + 1;
                    Integer nr = mm;
                    st.addtreefac(431, nr.toString(), 1, -1, 0, 0, 1, 0.0,
                            20.0, xp, yp, 0.0, 0, 0, 0, 1.0);
                    yp = yp + yab;
                } while (yp < 2.0);
                xp = xp + xab;
            } while (xp < 5.0);

        } catch (Exception e) {
            logger.error("Problem during tree generation", e);
        }

        logger.info("Trees generated : " + " " + st.ntrees);

        // Add environmental variables
        Tree envVar = new Tree();
        envVar.cw = awc;
        envVar.c66xy = prec;
        envVar.si = temp;
        envVar.cb = doyPlant;

        // Estimate mean stand height
        //DiameterDistributionXM function is currently superimposed with mean height estimation after year 1
        st.sp[0].h100 = fi.getValueForTree(envVar, st.tr[0].sp.spDef.diameterDistributionXML);

        //UniformHeightCurve is currently superimposed with survival rate after planting
        double survRate = this.fi.getValueForTree(envVar, st.tr[0].sp.spDef.uniformHeightCurveXML); // survInitGLM
        for (int i = 0; i < st.ntrees; i++) {
            if (Math.random() > survRate) st.tr[i].out = 1;
        }

        // Generate tree heights
        for (int i = 0; i < st.ntrees; i++) {
            if (st.tr[i].out == -1) {
                st.tr[i].h = fi.getValueForSpecies(st.tr[i].sp, st.tr[i].sp.spDef.volumeFunctionXML, st.random);
                st.tr[i].cw = awc;
                st.tr[i].c66xy = prec;
                st.tr[i].si = temp;
                st.tr[i].d = bz;
                st.tr[i].volumeDeadwood = st.tr[i].h; // Height increment
                //System.out.println(st.tr[i].h);
            }
        }

        return st;
    }

}
