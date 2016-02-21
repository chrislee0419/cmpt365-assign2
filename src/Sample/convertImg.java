package Sample;

import java.io.File;
import java.io.IOException;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
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
        m_panelImgInput.setSize(400, 400);
        m_panelImgInput.setBorder(BorderFactory.createLineBorder(Color.black));
	    totalGUI.add(m_panelImgInput);
	    
	    JLabel img_label = new JLabel();
	    img_label.setText("Original Picture");
	    img_label.setBounds(110, 420, 200, 30);
	    img_label.setFont(new Font("Verdana", 1, 16));
	    totalGUI.add(img_label);
	    
	    // create a panel for buttons
	    JPanel panelButtons = new JPanel();
	    panelButtons.setLayout(null);
	    panelButtons.setLocation(420, 50);
	    panelButtons.setSize(100, 160);
        totalGUI.add(panelButtons);
        
        m_panelImgOutputY = new IMGPanel();
        m_panelImgOutputY.setLocation(540, 10);
        m_panelImgOutputY.setSize(200, 200);
        m_panelImgOutputY.setBorder(BorderFactory.createLineBorder(Color.black));
        totalGUI.add(m_panelImgOutputY);
        
        JLabel y_label = new JLabel();
	    y_label.setText("Y Component");
	    y_label.setBounds(590, 220, 100, 20);
	    y_label.setFont(new Font("Verdana", 1, 12));
	    totalGUI.add(y_label);

        m_panelImgOutputU = new IMGPanel();
        m_panelImgOutputU.setLocation(750, 10);
        m_panelImgOutputU.setSize(200, 200);
        m_panelImgOutputU.setBorder(BorderFactory.createLineBorder(Color.black));
        totalGUI.add(m_panelImgOutputU);
        
        JLabel u_label = new JLabel();
	    u_label.setText("U Component");
	    u_label.setBounds(800, 220, 100, 20);
	    u_label.setFont(new Font("Verdana", 1, 12));
	    totalGUI.add(u_label);

        m_panelImgOutputV = new IMGPanel();
        m_panelImgOutputV.setLocation(960, 10);
        m_panelImgOutputV.setSize(200, 200);
        m_panelImgOutputV.setBorder(BorderFactory.createLineBorder(Color.black));
        totalGUI.add(m_panelImgOutputV);
        
        JLabel v_label = new JLabel();
	    v_label.setText("V Component");
	    v_label.setBounds(1010, 220, 100, 20);
	    v_label.setFont(new Font("Verdana", 1, 12));
	    totalGUI.add(v_label);
	    
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
	
	/////////////////////
	// CONVERSION CODE //
	/////////////////////
	
	private void RGBtoYUV() {
		if(m_imgInput == null)
    		return;
    	int w = m_imgInput.getWidth(null);
    	int h = m_imgInput.getHeight(null);
    	
    	// calculate YUV values
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
        
        // subsample the U and V chrominance components
        int[] u_values_sub = subsample(UValues, w, h);
        int[] v_values_sub = subsample(VValues, w, h);
        int w_sub = w/2 + w%2;
        int h_sub = h/2 + h%2;
    	
        // write Y values to the first output image
        m_imgOutputY = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
    	WritableRaster raster = (WritableRaster) m_imgOutputY.getData();
    	raster.setPixels(0, 0, w, h, YValues);
    	m_imgOutputY.setData(raster);
    	m_panelImgOutputY.setBufferedImage(m_imgOutputY);	

        // write U values to the second output image
        m_imgOutputU = new BufferedImage(w_sub, h_sub, BufferedImage.TYPE_BYTE_GRAY);
        raster = (WritableRaster) m_imgOutputU.getData();
        raster.setPixels(0, 0, w_sub, h_sub, u_values_sub);
        m_imgOutputU.setData(raster);
        m_panelImgOutputU.setBufferedImage(m_imgOutputU);    

        // write V values to the third output image
        m_imgOutputV = new BufferedImage(w_sub, h_sub, BufferedImage.TYPE_BYTE_GRAY);
        raster = (WritableRaster) m_imgOutputV.getData();
        raster.setPixels(0, 0, w_sub, h_sub, v_values_sub);
        m_imgOutputV.setData(raster);
        m_panelImgOutputV.setBufferedImage(m_imgOutputV);
	}

	// takes an average of 2 by 2 blocks to reduce chroma resolution
	// divide width and height by 2, take ceiling
	private int[] subsample(int[] values, int w, int h) {
		boolean odd_width = (w % 2 == 1);
		boolean odd_height = (h % 2 == 1);
		int[] result = new int[(w/2 + w%2) * (h/2 + h%2)];
		int holder;
		
		for (int y = 0; y < (h/2); y++) {
			for (int x = 0; x < (w/2); x++) {
				holder = 0;
				holder += values[x*2 + (y*2)*w];
				holder += values[x*2 + (y*2)*w + 1];
				holder += values[x*2 + (y*2 + 1)*w];
				holder += values[x*2 + (y*2 + 1)*w + 1];
				holder /= 4;
				result[x + y*(w/2 + w%2)] = holder;
			}
			if (odd_width) {
				holder = 0;
				holder += values[(y+1)*w - 1];
				holder += values[(y+2)*w - 1];
				holder /= 2;
				result[(y+1)*(w/2 + w%2) - 1] = holder;
			}
		}
		return result;
	}
	
	private int[] expandSubsample(int[] values, int w, int h) {
		// placeholder
		return new int[1];
	}
	
	private void YUVtoRGB() {
		// placeholder
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
        // convert RGB to YUV 
        else if(evnt.getSource() == m_btConvert){
        	RGBtoYUV();
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
        JFrame.setDefaultLookAndFeelDecorated(false);
        JFrame frame = new JFrame("JPEG Compression");

        //Create and set up the content pane.
        convertImg demo = new convertImg();
        frame.setContentPane(demo.createContentPane());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 500);
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
