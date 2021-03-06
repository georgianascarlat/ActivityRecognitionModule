package models;

public enum PostureClass {
    generalPosture("generalPosture"),
    torsoFirst("torsoFirst"),
    torsoSecond("torsoSecond"),
    head("head"),
    leftHandFirst("leftHandFirst"),
    rightHandFirst("rightHandFirst"),
    leftHandSecond("leftHandSecond"),
    rightHandSecond("rightHandSecond"),
    leftLegFirst("leftLegFirst"),
    rightLegFirst("rightLegFirst"),
    leftLegSecond("leftLegSecond"),
    rightLegSecond("rightLegSecond");


    private final String name;


    private PostureClass(String s) {
        name = s;
    }


    public String toString() {
        return name;
    }
}
