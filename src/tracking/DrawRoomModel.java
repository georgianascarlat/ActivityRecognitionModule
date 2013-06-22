package tracking;

import models.ObjectClass;
import models.RoomModel;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

public class DrawRoomModel extends Applet implements MouseListener, KeyListener {

    public static final int TEXT_REGION_HEIGHT = 15;
    public static final String ROOM_MODEL_TXT = "room_model.txt";
    private final double width = 1000.5, height = 5000;
    private final int widthParts = 20, heightParts = 15;
    private double widthChunkScaled, heightChunkScaled;
    private RoomModel roomModel;

    private Integer xClick, yClick;

    private Image bi;
    private Graphics2D big;
    private Integer objectIndex = 1;

    public DrawRoomModel() {
        roomModel = new RoomModel(new RoomInfo(width, height, widthParts, heightParts));
    }

    @Override
    public void init() {
        super.init();

        widthChunkScaled = (double) getWidth() / widthParts;
        heightChunkScaled = (double) (getHeight() - TEXT_REGION_HEIGHT) / heightParts;

        addMouseListener(this);

        addKeyListener(this);

        //double buffering
        bi = createImage(getWidth(), getHeight());
        big = (Graphics2D) bi.getGraphics();

        big.setColor(Color.white);

        big.clearRect(0, 0, getWidth(), getHeight());

        big.setBackground(Color.black);


    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D gg;
        int x, y;

        gg = (Graphics2D) g;

        big.setColor(Color.white);

        big.clearRect(0, 0, getWidth(), getHeight());

        big.setBackground(Color.black);

        big.setColor(Color.RED);
        big.drawString("Object index: " + objectIndex + " (" + ObjectClass.getObjectByIndex(objectIndex) + ")", 5, 12);

        for (int line = 0; line < heightParts; line++) {
            for (int column = 0; column < widthParts; column++) {

                x = (int) (column * widthChunkScaled);
                y = (int) (line * heightChunkScaled);


                if (xClick != null && yClick != null && xClick.equals(column) && yClick.equals(line)) {

                    big.setColor(Color.green);
                    big.fillRect(x, y + TEXT_REGION_HEIGHT, (int) widthChunkScaled, (int) heightChunkScaled);
                } else {
                    big.setColor(Color.blue);
                    big.drawRect(x, y + TEXT_REGION_HEIGHT, (int) widthChunkScaled, (int) heightChunkScaled);
                }

            }
        }


        gg.drawImage(bi, 0, 0, this);
    }


    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {

        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                try {

                    roomModel.saveModel(ROOM_MODEL_TXT);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Cannot save room model!");
                }
                System.exit(0);
            case KeyEvent.VK_1:
                objectIndex = 1;
                break;
            case KeyEvent.VK_2:
                objectIndex = 2;
                break;
            case KeyEvent.VK_3:
                objectIndex = 3;
                break;
            case KeyEvent.VK_4:
                objectIndex = 4;
                break;
            default:
                break;
        }

        repaint();


    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

        if (mouseEvent.getY() - TEXT_REGION_HEIGHT < 0)
            return;

        xClick = (int) (mouseEvent.getX() / widthChunkScaled);
        yClick = (int) ((mouseEvent.getY() - TEXT_REGION_HEIGHT) / heightChunkScaled);

        roomModel.setPointOnMap(yClick, xClick, objectIndex);

        repaint();
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
    }
}
