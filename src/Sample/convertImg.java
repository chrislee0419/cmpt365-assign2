package Sample;

import java.io.File;
import java.io.IOException;
import java.awt.AWTException;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;

import javax.swing.*;
import javax.imageio.*;

public class convertImg extends JFrame implements ActionListener{
	
	JButton m_btOpen, m_btSave, m_btConvert; 
	IMGPanel  m_panelImgInput, m_panelImgOutputY, m_panelImgOutputU, m_panelImgOutputV;
	BufferedImage m_imgInput, m_imgOutputY, m_imgOutputU, m_imgOutputV;
	//Create a file chooser
	final JFileChooser m_fc = new JFileChooser();
	
	//setup some GUI stuff
	public JPanel createContentPane (){	    
	    
		// We create a bottom JPanel to place everything on.
        JPanel totalGUI = new JPanel();
        totalGUI.setLayout(null);
	    
        m_panelImgInput = new IMGPanel();        
        m_panelImgInput.setLocation(10, 10);
        m_panelImgInput.setSize(400, 300);
	    totalGUI.add(m_panelImgInput);
	    
	    // create a panel for buttons
	    JPanel panelButtons = new JPanel();
	    panelButtons.setLayout(null);
	    panelButtons.setLocation(420, 50);
	    panelButtons.setSize(100, 160);
        totalGUI.add(panelButtons);
        
        m_panelImgOutputY = new IMGPanel();
        m_panelImgOutputY.setLocation(530, 10);
        m_panelImgOutputY.setSize(400, 300);
        totalGUI.add(m_panelImgOutputY);

        m_panelImgOutputU = new IMGPanel();
        m_panelImgOutputU.setLocation(940, 10);
        m_panelImgOutputU.setSize(400, 300);
        totalGUI.add(m_panelImgOutputU);

        m_panelImgOutputV = new IMGPanel();
        m_panelImgOutputV.setLocation(1350, 10);
        m_panelImgOutputV.setSize(400, 300);
        totalGUI.add(m_panelImgOutputV);
	    
	    m_btOpen = new JButton("OPEN");
	    m_btOpen.setLocation(0, 0);
	    m_btOpen.setSize(100, 40);
	    m_btOpen.addActionListener(this);
	    panelButtons.add(m_btOpen);
	    
	    m_btSave = new JButton("SAVE");
	    m_btSave.setLocation(0, 60);
	    m_btSave.setSize(100, 40);
	    m_btSave.addActionListener(this);
	    panelButtons.add(m_btSave);
	    
	    m_btConvert = new JButton("RGB->YUV");
	    m_btConvert.setLocation(0, 120);
	    m_btConvert.setSize(100, 40);
	    m_btConvert.addActionListener(this);
	    panelButtons.add(m_btConvert);
	    	    
	    totalGUI.setOpaque(true);
	    return totalGUI;
	}
	
    // This is the new ActionPerformed Method.
    // It catches any events with an ActionListener attached.
    // Using an if statement, we can determine which button was pressed
    // and change the appropriate values in our GUI.
    public void actionPerformed(ActionEvent evnt) {
        // button OPEN is clicked
    	if(evnt.getSource() == m_btOpen){
        	m_fc.addChoosableFileFilter(new ImageFilter());
        	m_fc.setAcceptAllFileFilterUsed(false);
        	int returnVal = m_fc.showOpenDialog(convertImg.this);
        	if (returnVal == JFileChooser.APPROVE_OPTION) {
                 File file = m_fc.getSelectedFile();
                 try {
                	 m_imgInput = ImageIO.read(file);
                     m_panelImgInput.setBufferedImage(m_imgInput);	
                 }catch (IOException ex) {
                	 //...
                 }
            }
        }
        // convert RGB to Y 
        else if(evnt.getSource() == m_btConvert){
        	if(m_imgInput == null)
        		return;
        	int w = m_imgInput.getWidth(null);
        	int h = m_imgInput.getHeight(null);
        	
        	// calculate Y values
        	int YValues[] = new int[w*h];
            int UValues[] = new int[w*h];
            int VValues[] = new int[w*h];
        	int inputValues[] = new int[w*h];
        	PixelGrabber grabber = new PixelGrabber(m_imgInput.getSource(), 0, 0, w, h, inputValues, 0, w);
            try{
              if(grabber.grabPixels() != true){
                try{
            	  throw new AWTException("Grabber returned false: " + grabber.status());
            	}catch (Exception e) {};
              }
            } catch (InterruptedException e) {};
            
            int red,green, blue; 
            for (int index = 0; index < h * w; ++index){
            	red = ((inputValues[index] & 0x00ff0000) >> 16);
            	green =((inputValues[index] & 0x0000ff00) >> 8);
            	blue = ((inputValues[index] & 0x000000ff) );
            	YValues[index] = (int)((0.299 * (float)red) + (0.587 * (float)green) + (0.114 * (float)blue));
                UValues[index] = (int)((-0.299* (float)red) + (-0.587* (float)green) + (0.866 * (float)blue));
                VValues[index] = (int)((0.701 * (float)red) + (-0.587* (float)green) + (-0.114* (float)blue));
            }
        	
            // write Y values to the first output image
            m_imgOutputY = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        	WritableRaster raster = (WritableRaster) m_imgOutputY.getData();
        	raster.setPixels(0, 0, w, h, YValues);
        	m_imgOutputY.setData(raster);
        	m_panelImgOutputY.setBufferedImage(m_imgOutputY);	

            // write U values to the second output image
            m_imgOutputU = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
            raster = (WritableRaster) m_imgOutputU.getData();
            raster.setPixels(0, 0, w, h, UValues);
            m_imgOutputU.setData(raster);
            m_panelImgOutputU.setBufferedImage(m_imgOutputU);    

            // write V values to the third output image
            m_imgOutputV = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
            raster = (WritableRaster) m_imgOutputV.getData();
            raster.setPixels(0, 0, w, h, VValues);
            m_imgOutputV.setData(raster);
            m_panelImgOutputV.setBufferedImage(m_imgOutputV);    
        }
        // button SAVE is clicked
        else if(evnt.getSource() == m_btSave){
        	if(m_imgOutputY == null)
        		return;
        	m_fc.addChoosableFileFilter(new ImageFilter());
        	m_fc.setAcceptAllFileFilterUsed(false);
        	int returnVal = m_fc.showSaveDialog(convertImg.this);
        	if (returnVal == JFileChooser.APPROVE_OPTION) {
        		File file = m_fc.getSelectedFile();	
        		try {
            	    ImageIO.write(m_imgOutputY, "jpg", file);
            	} catch (IOException e) {
            		//...
            	}
        	}
        }
    }
	
    private static void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("JPEG Compression");

        //Create and set up the content pane.
        convertImg demo = new convertImg();
        frame.setContentPane(demo.createContentPane());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1770, 360);
        frame.setVisible(true);
    }
    
	public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
