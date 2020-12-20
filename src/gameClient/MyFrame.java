package gameClient;

import api.directed_weighted_graph;
import api.edge_data;
import api.geo_location;
import api.node_data;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a very simple GUI class to present a
 * game on a graph - you are welcome to use this class - yet keep in mind
 * that the code is not well written in order to force you improve the
 * code and not to take it "as is".
 */
public class MyFrame extends JFrame {
    private int _ind;
    private Arena _ar;
    private long Moves, Time;
    private int Grade;
    private edge_data[] AgentsEdge;
    private gameClient.util.Range2Range _w2f;

    MyFrame(String a) {
        super(a);
        int _ind = 0;
    }

    public void update(Arena ar) {
        this._ar = ar;
        updateFrame();
    }

    public void SetCounter(int G, long M, long T) {
        Time = T;
        Grade = G;
        Moves = M;
    }

    public void SetColoredEdge(edge_data[] e) {
        AgentsEdge = e;
    }

    private void updateFrame() {
        Range rx = new Range(20, this.getWidth() - 20);
        Range ry = new Range(this.getHeight() - 10, 150);
        Range2D frame = new Range2D(rx, ry);
        directed_weighted_graph g = _ar.getGraph();
        _w2f = Arena.w2f(g, frame);
    }

    public void paint(Graphics g) {

        int w = this.getWidth();
        int h = this.getHeight();

        Image buffer_image;
        Graphics buffer_graphics;
        // Create a new "canvas"
        buffer_image = createImage(w, h);
        buffer_graphics = buffer_image.getGraphics();
        paintComponents(buffer_graphics);
        // Draw on the new "canvas"
        // "Switch" the old "canvas" for the new one
        g.drawImage(buffer_image, 0, 0, this);

    }

    @Override
    public void paintComponents(Graphics g) {
        int w = this.getWidth();
        int h = this.getHeight();
        g.clearRect(0, 0, w, h);
        ImageIcon background = new ImageIcon("Imj/background.jpg");
        BufferedImage resizedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(background.getImage(), 0, 0, w, h, null);
        graphics2D.dispose();
        g.drawImage(resizedImage, 0, 0, background.getImageObserver());
        updateFrame();
        drawData(g, w, h);
        drawGraph(g);
        drawPokemons(g);
        drawAgants(g);
        drawInfo(g);

    }

    private void drawInfo(Graphics g) {

        List<String> str = _ar.get_info();
        String dt = "none";
        for (int i = 0; i < str.size(); i++) {
            g.drawString(str.get(i) + " dt: " + dt, 100, 60 + i * 20);
        }

    }

    private void drawData(Graphics g, int w, int h) {
        g.setColor(Color.white);
        g.setFont(new Font("Serif", Font.PLAIN, 14));
        g.drawString("Remainig Time :  " + Time + "  Secoends", w / 2, h / 9);
        g.drawString("your's Grade is :  " + Grade, w / 10, h / 9);
        g.drawString("Num Of Moves is :  " + Moves, w / 4, h / 9);
    }

    public void ImageDraw(Graphics g, int x, int y, ImageIcon ImageBuffer) {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(ImageBuffer.getImage(), x, y, ImageBuffer.getImageObserver());
    }

    private void drawGraph(Graphics g) {
        directed_weighted_graph gg = _ar.getGraph();
        Iterator<node_data> iter = gg.getV().iterator();
        while (iter.hasNext()) {
            node_data n = iter.next();
            g.setColor(Color.white);
            drawNode(n, 5, g);
            Iterator<edge_data> itr = gg.getE(n.getKey()).iterator();
            while (itr.hasNext()) {
                edge_data e = itr.next();
                g.setColor(Color.white);
                if (AgentsEdge != null) {

                    for (int i = 0; i < AgentsEdge.length; i++) {
                        edge_data E = AgentsEdge[i];
                        if (E != null && ((E.getSrc() == e.getSrc()) && (E.getDest() == e.getDest())) || ((E.getSrc() == e.getDest()) && (E.getDest() == e.getSrc()))) {
                            if (i == 0) g.setColor(Color.BLUE);
                            else if (i == 1) g.setColor(Color.red);
                            else if (i == 2) g.setColor(Color.GREEN);
                            else if (i == 3) g.setColor(Color.CYAN);
                            else g.setColor(Color.black);
                        }

                    }
                }
                drawEdge(e, g);
            }
        }
    }

    private void drawPokemons(Graphics g) {
        List<CL_Pokemon> fs = _ar.getPokemons();
        if (fs != null) {
            Iterator<CL_Pokemon> itr = fs.iterator();

            while (itr.hasNext()) {

                CL_Pokemon f = itr.next();
                Point3D c = f.getLocation();
                int r = 20;
                if (c != null) {
                    ImageIcon I = f.GetIcon(f.getType());
                    geo_location fp = this._w2f.world2frame(c);
                    g.drawImage(I.getImage(), (int) fp.x() - r, (int) fp.y() - r, 2 * r, 2 * r, I.getImageObserver());
                    //g.fillOval((int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r);
                    //	g.drawString(""+n.getKey(), fp.ix(), fp.iy()-4*r);

                }
            }
        }
    }

    private void drawAgants(Graphics g) {
        List<CL_Agent> rs = _ar.getAgents();
        //	Iterator<OOP_Point3D> itr = rs.iterator();
        g.setColor(Color.red);
        int i = 0;
        while (rs != null && i < rs.size()) {
            geo_location c = rs.get(i).getLocation();
            int r = 15;
            if (c != null) {

                geo_location fp = this._w2f.world2frame(c);
                //g.fillOval((int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r);
                ImageIcon I = rs.get(i).GetIcon();
                g.drawImage(I.getImage(), (int) fp.x() - r, (int) fp.y() - r, 2 * r, 2 * r, I.getImageObserver());
            }
            i++;
        }
    }

    private void drawNode(node_data n, int r, Graphics g) {
        geo_location pos = n.getLocation();
        geo_location fp = this._w2f.world2frame(pos);
        g.fillOval((int) fp.x() - r, (int) fp.y() - r, 2 * r, 2 * r);
        g.drawString("" + n.getKey(), (int) fp.x(), (int) fp.y() - 4 * r);
    }

    private void drawEdge(edge_data e, Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));
        directed_weighted_graph gg = _ar.getGraph();
        geo_location s = gg.getNode(e.getSrc()).getLocation();
        geo_location d = gg.getNode(e.getDest()).getLocation();
        geo_location s0 = this._w2f.world2frame(s);
        geo_location d0 = this._w2f.world2frame(d);
        g.drawLine((int) s0.x(), (int) s0.y(), (int) d0.x(), (int) d0.y());
        //	g.drawString(""+n.getKey(), fp.ix(), fp.iy()-4*r);
    }
}