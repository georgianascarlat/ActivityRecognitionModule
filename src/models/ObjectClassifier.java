package models;


public enum ObjectClassifier {

    OFFICE_CHAIR("office_chair_cascade.xml", 1, ObjectClass.CHAIR),
    WINDSOR_CHAIR("windsor_chair_cascade.xml", 2, ObjectClass.CHAIR),
    IRON_BED("iron_bed_cascade.xml", 3, ObjectClass.BED);

    private int index;
    private String fileName;
    private ObjectClass objectClass;

    private ObjectClassifier(String fileName, int index, ObjectClass objectClass) {
        this.index = index;
        this.fileName = fileName;
        this.objectClass = objectClass;
    }

    public String getFileName() {
        return fileName;
    }

    public int getIndex() {
        return index;
    }

    public ObjectClass getObjectClass() {
        return objectClass;
    }

    @Override
    public String toString() {
        return "ObjectClassifier{" +
                "index=" + index +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
