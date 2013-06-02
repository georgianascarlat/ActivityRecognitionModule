package app;


import models.RoomModel;
import tracking.RoomInfo;
import utils.Utils;

import java.io.IOException;

public class CreateRoomModel {

    public static final int widthParts = 100, heightParts = 200;
    public static final double width = 1000, height = 5000;

    public static void main(String args[]) throws IOException {

        //TODO: scan room for objects and create model

        new RoomModel(new RoomInfo(width, height, widthParts, heightParts)).createModel(Utils.ROOM_MODEL_FILE);
    }
}
