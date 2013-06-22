package models;


import tracking.RoomInfo;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class RoomModel {

    private RoomInfo roomInfo;
    private int gridMap[][];

    public RoomModel(RoomInfo roomInfo) {
        this.roomInfo = roomInfo;

        gridMap = new int[roomInfo.getHeightParts()][roomInfo.getWidthParts()];

        for (int i = 0; i < roomInfo.getHeightParts(); i++) {
            for (int j = 0; j < roomInfo.getWidthParts(); j++) {
                gridMap[i][j] = ObjectClass.NO_OBJECT.getIndex();
            }
        }
    }

    public RoomModel(String fileName) throws FileNotFoundException {

        loadModel(fileName);
    }

    public void createModel(String fileName) throws IOException {
        //TODO: create room model

        saveModel(fileName);
    }

    public int getPointOnMap(int line, int column) {
        return gridMap[line][column];
    }

    public void setPointOnMap(int line, int column, int objectIndex) {
        if (ObjectClass.checkIndex(objectIndex))
            gridMap[line][column] = objectIndex;
    }

    public double getFloorWidth() {
        return roomInfo.getWidth();
    }

    public double getFloorHeight() {
        return roomInfo.getHeight();
    }

    public int getWidthParts() {
        return roomInfo.getWidthParts();
    }

    public int getHeightParts() {
        return roomInfo.getHeightParts();
    }

    public void loadModel(String fileName) throws FileNotFoundException {

        Scanner scanner = new Scanner(new File(fileName));
        double width = scanner.nextDouble(), height = scanner.nextDouble();
        int widthParts = scanner.nextInt(), heightParts = scanner.nextInt();

        this.roomInfo = new RoomInfo(width, height, widthParts, heightParts);
        this.gridMap = new int[roomInfo.getHeightParts()][roomInfo.getWidthParts()];


        for (int i = 0; i < heightParts; i++) {
            for (int j = 0; j < widthParts; j++) {
                this.gridMap[i][j] = scanner.nextInt();
            }
        }

        scanner.close();

    }

    public void saveModel(String fileName) throws IOException {
        BufferedWriter out = null;
        int width = roomInfo.getWidthParts(), height = roomInfo.getHeightParts();

        try {


            out = new BufferedWriter(new FileWriter(fileName));

            out.write(roomInfo.getWidth() + " " + roomInfo.getHeight() + "\n");
            out.write(roomInfo.getWidthParts() + " " + roomInfo.getHeightParts() + "\n");

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    out.write(gridMap[i][j] + " ");
                }
                out.write("\n");
            }


        } finally {
            if (null != out)
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    public String toString() {
        return "RoomModel{" +
                "roomInfo=" + roomInfo +
                ", gridMap=" + Arrays.toString(gridMap) +
                '}';
    }
}
