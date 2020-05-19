package main.emfk.com.emfklatest;

/**
 * Created by anthony.anyama on 12/1/2016.
 */

public class Algorithms {

    private String algorithm;
    private String algoritmFile;

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getAlgoritmFile() {
        return algoritmFile;
    }

    public void setAlgoritmFile(String algoritmFile) {
        this.algoritmFile = algoritmFile;
    }

    public Algorithms(){

    }

    public Algorithms(String algorithmName, String filename){
        this.algorithm = algorithmName;
        this.algoritmFile = filename;
    }
}
