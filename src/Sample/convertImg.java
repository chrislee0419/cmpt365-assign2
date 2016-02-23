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
	IMGPanel	m_panelImgInput, compressed_output,
				m_panelImgOutputY, m_panelImgOutputU, m_panelImgOutputV,
				compressed_y_panel, compressed_u_panel, compressed_v_panel;
	BufferedImage 	m_imgInput, m_imgOutputY, m_imgOutputU, m_imgOutputV,
					compressed_y_buffer, compressed_u_buffer, compressed_v_buffer, compressed_output_buffer;
	//Create a file chooser
	final JFileChooser m_fc = new JFileChooser();
	
	float[] y_values, u_values, v_values;
    static Matrix[][] dct_mat;
    static Matrix[][] quantized_mat;
    
    final static int window_width = 1800, window_height = 980;
	
	//setup some GUI stuff
	public JPanel createContentPane (){	    
	    
		// We create a bottom JPanel to place everything on.
        JPanel totalGUI = new JPanel();
        totalGUI.setLayout(null);
	    
        // ORIGINAL PICTURE
        m_panelImgInput = new IMGPanel();        
        m_panelImgInput.setLocation(10, 10);
        m_panelImgInput.setSize(400, 400);
        m_panelImgInput.setBorder(BorderFactory.createLineBorder(Color.black));
	    totalGUI.add(m_panelImgInput);
	    
	    JLabel input_label = new JLabel();
	    input_label.setText("Original Picture");
	    input_label.setBounds(110, 420, 200, 30);
	    input_label.setFont(new Font("Verdana", 1, 16));
	    totalGUI.add(input_label);
	    
	    // COMPRESSED PICTURE
	    compressed_output = new IMGPanel();        
	    compressed_output.setLocation(10, 460);
	    compressed_output.setSize(400, 400);
	    compressed_output.setBorder(BorderFactory.createLineBorder(Color.black));
	    totalGUI.add(compressed_output);
	    
	    JLabel compressed_output_label = new JLabel();
	    compressed_output_label.setText("Compressed Picture");
	    compressed_output_label.setBounds(110, 870, 200, 30);
	    compressed_output_label.setFont(new Font("Verdana", 1, 16));
	    totalGUI.add(compressed_output_label);
	    
	    // create a panel for buttons
	    JPanel panelButtons = new JPanel();
	    panelButtons.setLayout(null);
	    panelButtons.setLocation(420, 50);
	    panelButtons.setSize(100, 160);
        totalGUI.add(panelButtons);
        
        // Y COMPONENT
        m_panelImgOutputY = new IMGPanel();
        m_panelImgOutputY.setLocation(540, 10);
        m_panelImgOutputY.setSize(400, 400);
        m_panelImgOutputY.setBorder(BorderFactory.createLineBorder(Color.black));
        totalGUI.add(m_panelImgOutputY);
        
        JLabel y_label = new JLabel();
	    y_label.setText("Y Component");
	    y_label.setBounds(590, 420, 100, 30);
	    y_label.setFont(new Font("Verdana", 1, 16));
	    totalGUI.add(y_label);
	    
	    // COMPRESSED Y COMPONENT
        compressed_y_panel = new IMGPanel();
        compressed_y_panel.setLocation(540, 460);
        compressed_y_panel.setSize(400, 400);
        compressed_y_panel.setBorder(BorderFactory.createLineBorder(Color.black));
        totalGUI.add(compressed_y_panel);
        
        JLabel compressed_y_label = new JLabel();
	    compressed_y_label.setText("Compressed Y Component");
	    compressed_y_label.setBounds(640, 870, 200, 30);
	    compressed_y_label.setFont(new Font("Verdana", 1, 16));
	    totalGUI.add(compressed_y_label);

        // U COMPONENT
        m_panelImgOutputU = new IMGPanel();
        m_panelImgOutputU.setLocation(950, 10);
        m_panelImgOutputU.setSize(400, 400);
        m_panelImgOutputU.setBorder(BorderFactory.createLineBorder(Color.black));
        totalGUI.add(m_panelImgOutputU);
        
        JLabel u_label = new JLabel();
	    u_label.setText("U Component");
	    u_label.setBounds(1050, 420, 200, 30);
	    u_label.setFont(new Font("Verdana", 1, 16));
	    totalGUI.add(u_label);
	    
	    // COMPRESSED U COMPONENT
        compressed_u_panel = new IMGPanel();
        compressed_u_panel.setLocation(950, 460);
        compressed_u_panel.setSize(400, 400);
        compressed_u_panel.setBorder(BorderFactory.createLineBorder(Color.black));
        totalGUI.add(compressed_u_panel);
        
        JLabel compressed_u_label = new JLabel();
	    compressed_u_label.setText("Compressed U Component");
	    compressed_u_label.setBounds(1050, 870, 200, 30);
	    compressed_u_label.setFont(new Font("Verdana", 1, 16));
	    totalGUI.add(compressed_u_label);

		// V COMPONENT
        m_panelImgOutputV = new IMGPanel();
        m_panelImgOutputV.setLocation(1360, 10);
        m_panelImgOutputV.setSize(400, 400);
        m_panelImgOutputV.setBorder(BorderFactory.createLineBorder(Color.black));
        totalGUI.add(m_panelImgOutputV);
        
        JLabel v_label = new JLabel();
	    v_label.setText("V Component");
	    v_label.setBounds(1460, 420, 200, 30);
	    v_label.setFont(new Font("Verdana", 1, 16));
	    totalGUI.add(v_label);
	    
	    // COMPRESSED V COMPONENT
        compressed_v_panel = new IMGPanel();
        compressed_v_panel.setLocation(1360, 460);
        compressed_v_panel.setSize(400, 400);
        compressed_v_panel.setBorder(BorderFactory.createLineBorder(Color.black));
        totalGUI.add(compressed_v_panel);
        
        JLabel compressed_v_label = new JLabel();
	    compressed_v_label.setText("Compressed V Component");
	    compressed_v_label.setBounds(1460, 870, 200, 30);
	    compressed_v_label.setFont(new Font("Verdana", 1, 16));
	    totalGUI.add(compressed_v_label);
	    
	    // BUTTONS
	    m_btOpen = new JButton("Open");
	    m_btOpen.setLocation(0, 0);
	    m_btOpen.setSize(100, 40);
	    m_btOpen.addActionListener(this);
	    panelButtons.add(m_btOpen);
	    
	    m_btSave = new JButton("Save");
	    m_btSave.setLocation(0, 60);
	    m_btSave.setSize(100, 40);
	    m_btSave.addActionListener(this);
	    panelButtons.add(m_btSave);
	    
	    m_btConvert = new JButton("Compress");
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
	
	private void conversion() {
		if(m_imgInput == null)
    		return;
    	int w = m_imgInput.getWidth(null);
    	int h = m_imgInput.getHeight(null);

    	int inputValues[] = new int[w*h];
    	
    	PixelGrabber grabber = new PixelGrabber(m_imgInput.getSource(), 0, 0, w, h, inputValues, 0, w);
        try{
          if(grabber.grabPixels() != true){
            try{
        	  throw new AWTException("Grabber returned false: " + grabber.status());
        	}catch (Exception e) {};
          }
        } catch (InterruptedException e) {};
        
        RGBtoYUV(inputValues, w, h);
        
        // subsample the U and V chrominance components
        float[] u_values_sub = subsample(u_values, w, h);
        float[] v_values_sub = subsample(v_values, w, h);
        int w_sub = w/2 + w%2;
        int h_sub = h/2 + h%2;
        
        // use JPEG compression
        float[] compressed_y = compressComponent(y_values, w, h, 1);
        float[] compressed_u = compressComponent(u_values_sub, w_sub, h_sub, 2);
        float[] compressed_v = compressComponent(v_values_sub, w_sub, h_sub, 2);
        
        // convert back to 8 bit for previewing
//        int[] converted_y = new int[w*h];
//        int[] converted_u = new int[w_sub*h_sub];
//        int[] converted_v = new int[w_sub*h_sub];
//        for (int i = 0; i < w*h; i++) {
//        	converted_y[i] = convertTo256(compressed_y[i]);
//        }
//        for (int i = 0; i < w_sub*h_sub; i++) {
//        	converted_u[i] = convertTo256(compressed_u[i]);
//        	converted_v[i] = convertTo256(compressed_v[i]);
//        }
        
        // expand the subsampled components back to their original resolution
        float[] expanded_compressed_u = expandSubsample(compressed_u, w, h);
        float[] expanded_compressed_v = expandSubsample(compressed_v, w, h);
        
        int[] output_picture = YUVtoRGB(inputValues, compressed_y, expanded_compressed_u, expanded_compressed_v, w, h);
    	
    	// NOT CONVERTED
        // write Y values to the first output image
        m_imgOutputY = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
    	WritableRaster raster = (WritableRaster) m_imgOutputY.getData();
    	raster.setPixels(0, 0, w, h, y_values);
    	m_imgOutputY.setData(raster);
    	m_panelImgOutputY.setBufferedImage(m_imgOutputY);	

        // write U values to the second output image
        m_imgOutputU = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        raster = (WritableRaster) m_imgOutputU.getData();
        raster.setPixels(0, 0, w, h, u_values);
        m_imgOutputU.setData(raster);
        m_panelImgOutputU.setBufferedImage(m_imgOutputU);    

        // write V values to the third output image
        m_imgOutputV = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        raster = (WritableRaster) m_imgOutputV.getData();
        raster.setPixels(0, 0, w, h, v_values);
        m_imgOutputV.setData(raster);
        m_panelImgOutputV.setBufferedImage(m_imgOutputV);
        
        // CONVERTED
        // write converted Y values to the first output image
        compressed_y_buffer = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
    	raster = (WritableRaster) compressed_y_buffer.getData();
    	raster.setPixels(0, 0, w, h, compressed_y);
    	compressed_y_buffer.setData(raster);
    	compressed_y_panel.setBufferedImage(compressed_y_buffer);	

        // write converted U values to the second output image
    	compressed_u_buffer = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        raster = (WritableRaster) compressed_u_buffer.getData();
        raster.setPixels(0, 0, w, h, expanded_compressed_u);
        compressed_u_buffer.setData(raster);
        compressed_u_panel.setBufferedImage(compressed_u_buffer);    

        // write converted V values to the third output image
        compressed_v_buffer = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        raster = (WritableRaster) compressed_v_buffer.getData();
        raster.setPixels(0, 0, w, h, expanded_compressed_v);
        compressed_v_buffer.setData(raster);
        compressed_v_panel.setBufferedImage(compressed_v_buffer);
        
        // FINAL RGB PICTURE
        compressed_output_buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        compressed_output_buffer.setRGB(0, 0, w, h, output_picture, 0, w);
//    	raster = (WritableRaster) compressed_output_buffer.getData();
//    	raster.setPixels(0, 0, w, h, output_picture);
//    	compressed_output_buffer.setData(raster);
    	compressed_output.setBufferedImage(compressed_output_buffer);	
        
	}
	
	private void RGBtoYUV(int[] input_values, int w, int h) {
    	// calculate YUV values
    	y_values = new float[w*h];
        u_values = new float[w*h];
        v_values = new float[w*h];
    	
        // convert 8 bit colour components to YUV components
        int red, green, blue;
        for (int index = 0; index < h * w; ++index){
        	red = ((input_values[index] & 0x00ff0000) >> 16) - 128;
        	green =((input_values[index] & 0x0000ff00) >> 8) - 128;
        	blue = ((input_values[index] & 0x000000ff) ) - 128;
        	
        	y_values[index] = (0.299f * (float)red) + (0.587f * (float)green) + (0.114f * (float)blue);
            u_values[index] = (-0.14713f * (float)red) + (-0.28886f * (float)green) + (0.436f * (float)blue);
            v_values[index] = (0.615f * (float)red) + (-0.51499f * (float)green) + (-0.10001f * (float)blue);
            
            y_values[index] = y_values[index] + 128;
            u_values[index] = u_values[index] + 128;
            v_values[index] = v_values[index] + 128;
        }
	}
	
	private int[] YUVtoRGB(int[] input, float[] y_arr, float[] u_arr, float[] v_arr, int w, int h) {
        // convert 8 bit colour components to YUV components
        int red, green, blue;
        float r, g, b, y, u, v;
        
        int[] rgb = new int[w*h];
        for (int index = 0; index < h * w; ++index) {
        	if (y_arr[index] < 0) y_arr[index] = 0f;
        	if (u_arr[index] < 0) u_arr[index] = 0f;
        	if (v_arr[index] < 0) v_arr[index] = 0f;
        	
        	y = y_arr[index] - 128;
        	u = u_arr[index] - 128;
        	v = v_arr[index] - 128;
        	
        	r = y + (1.13983f * v);
            g = y + (-0.39465f * u) + (-0.58060f * v);
            b = y + (2.03211f * u);
            
            red = (int)r + 128;
            green = (int)g + 128;
            blue = (int)b + 128;
            
            // normalize values, otherwise we'll get odd colours
            if (red < 0) red = 0;
            else if (red > 255) red = 255;
            if (blue < 0) blue = 0;
            else if (blue > 255) blue = 255;
            if (green < 0) green = 0;
            else if(green > 255) green = 255;
            
            rgb[index] = (input[index] & 0xff000000) | ((red << 16) & 0x00ff0000) | ((green << 8) & 0x0000ff00) | (blue & 0x000000ff);
//            rgb[index] = input[index];
//            rgb[index + w*h] = red;
//            rgb[index + 2*w*h] = green;
//            rgb[index + 3*w*h] = blue;
        }
            
        return rgb;
	}

    // split component into 8x8 block matrices
	private static float[] compressComponent(float[] arr, int w, int h, int quality) {
        Matrix[][] mat = Matrix.array1DTo2DMatrix(arr, w, h);
        dct_mat = new Matrix[h][w];
        quantized_mat = new Matrix[h][w];
        Matrix temp;
        
        for (int i = 0; i < Math.ceil((double)h/8); i++) {
        	for (int j = 0; j < Math.ceil((double)w/8); j++) {
        		dct_mat[i][j] = Transform.dctransform(mat[i][j]);
        		quantized_mat[i][j] = Quantization.quantize(dct_mat[i][j], quality);
        		temp = Quantization.inv_quantize(quantized_mat[i][j], quality);
        		mat[i][j] = Transform.inv_dctransform(temp);
        		mat[i][j].normalizeValues();
        	}
        }
        
        return Matrix.matrix2DTo1DArray(mat, w, h);
	}

	// takes an average of 2 by 2 blocks to reduce chroma resolution
	// divide width and height by 2, take ceiling
	private static float[] subsample(float[] values, int w, int h) {
		boolean odd_width = (w % 2 == 1);
		boolean odd_height = (h % 2 == 1);
		float[] result = new float[(w/2 + w%2) * (h/2 + h%2)];
		float holder;
		
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
		if (odd_height) {
			for (int x = 0; x < (w/2); x++) {
				holder = 0;
				holder += values[(h-1) * w + 2*x];
				holder += values[(h-1) * w + 2*x + 1];
				holder /= 2;
				result[(h/2)*(w/2 + w%2) + x] = holder;
			}
			if (odd_width) {
				result[(w/2 + w%2)*(h/2 + h%2) - 1] = values[w*h - 1];
			}
		}
		return result;
	}
	
	private float[] expandSubsample(float[] values, int w, int h) {
		boolean odd_width = (w % 2 == 1);
		boolean odd_height = (h % 2 == 1);
		float[] result = new float[w*h];
		
		for (int y = 0; y < (h/2); y++) {
			for (int x = 0; x < (w/2); x++) {
				result[2*y*w + 2*x] = values[y*(w/2 + w%2) + x];
				result[2*y*w + 2*x + 1] = values[y*(w/2 + w%2) + x];
				result[(2*y+1)*w + 2*x] = values[y*(w/2 + w%2) + x];
				result[(2*y+1)*w + 2*x + 1] = values[y*(w/2 + w%2) + x];
			}
			if (odd_width) {
				result[(2*y+1)*w - 1] = values[(y+1)*(w/2 + w%2) - 1];
				result[(2*y+2)*w - 1] = values[(y+1)*(w/2 + w%2) - 1];
			}
		}
		if (odd_height) {
			for (int x = 0; x < (w/2); x++) {
				result[(h-1)*w + 2*x] = values[(h/2 + h%2 - 1)*(w/2 + w%2) + x];
				result[(h-1)*w + 2*x + 1] = values[(h/2 + h%2 - 1)*(w/2 + w%2) + x];
			}
			if (odd_width) {
				result[w*h - 1] = values[(h/2 + h%2)*(w/2 + w%2) - 1];
			}
		}
		return result;
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
        	conversion();
        }
        // button SAVE is clicked
        else if(evnt.getSource() == m_btSave){
        	if(compressed_output_buffer == null)
        		return;
        	m_fc.addChoosableFileFilter(new ImageFilter());
        	m_fc.setAcceptAllFileFilterUsed(false);
        	int returnVal = m_fc.showSaveDialog(convertImg.this);
        	if (returnVal == JFileChooser.APPROVE_OPTION) {
        		File file = m_fc.getSelectedFile();	
        		try {
            	    ImageIO.write(compressed_output_buffer, "jpg", file);
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
        frame.setSize(window_width, window_height);
        frame.setVisible(true);
    }
    
	public static void main(String[] args) {
//		float[] arr = {
//				255, 255, 255, 255, 255, 255, 255, 255,		 0, 0, 0, 0, 0, 0, 0, 0,
//				255, 255, 255, 255, 255, 255, 255, 255,		 0, 0, 0, 0, 0, 0, 0, 0,
//				255, 255, 255, 255, 255, 255, 255, 255,		 0, 0, 0, 0, 0, 0, 0, 0,
//				255, 255, 255, 255, 255, 255, 255, 255,		 0, 0, 0, 0, 0, 0, 0, 0,
//				255, 255, 255, 255, 255, 255, 255, 255,		 0, 0, 0, 0, 0, 0, 0, 0,
//				255, 255, 255, 255, 255, 255, 255, 255,		 0, 0, 0, 0, 0, 0, 0, 0,
//				255, 255, 255, 255, 255, 255, 255, 255,		 0, 0, 0, 0, 0, 0, 0, 0,
//				255, 255, 255, 255, 255, 255, 255, 255,		 0, 0, 0, 0, 0, 0, 0, 0,
//				
//				0, 255, 0, 255, 0, 255, 0, 255,			 0, 255, 0, 255, 0, 255, 0, 255,
//				0, 255, 0, 255, 0, 255, 0, 255,			 255, 0, 255, 0, 255, 0, 255, 0,
//				0, 255, 0, 255, 0, 255, 0, 255,			 0, 255, 0, 255, 0, 255, 0, 255,
//				0, 255, 0, 255, 0, 255, 0, 255,			 255, 0, 255, 0, 255, 0, 255, 0,
//				0, 255, 0, 255, 0, 255, 0, 255,			 0, 255, 0, 255, 0, 255, 0, 255,
//				0, 255, 0, 255, 0, 255, 0, 255,			 255, 0, 255, 0, 255, 0, 255, 0,
//				0, 255, 0, 255, 0, 255, 0, 255,			 0, 255, 0, 255, 0, 255, 0, 255,
//				0, 255, 0, 255, 0, 255, 0, 255,			 255, 0, 255, 0, 255, 0, 255, 0,
//				
//				0, 64, 128, 255, 0, 64, 128, 255,		0, 64, 128, 255, 0, 64, 128, 255,
//				0, 64, 128, 255, 0, 64, 128, 255,		64, 128, 255, 0, 64, 128, 255, 0,
//				0, 64, 128, 255, 0, 64, 128, 255,		128, 255, 0, 64, 128, 255, 0, 64,
//				0, 64, 128, 255, 0, 64, 128, 255,		255, 0, 64, 128, 255, 0, 64, 128,
//				0, 64, 128, 255, 0, 64, 128, 255,		0, 64, 128, 255, 0, 64, 128, 255,
//				0, 64, 128, 255, 0, 64, 128, 255,		64, 128, 255, 0, 64, 128, 255, 0,
//				0, 64, 128, 255, 0, 64, 128, 255,		128, 255, 0, 64, 128, 255, 0, 64,
//				0, 64, 128, 255, 0, 64, 128, 255,		255, 0, 64, 128, 255, 0, 64, 128,
//				
//				64, 128, 64, 128, 64, 128, 64, 128, 	64, 128, 64, 128, 64, 128, 64, 128,
//				64, 128, 64, 128, 64, 128, 64, 128,		128, 64, 128, 64, 128, 64, 128, 64,
//				64, 128, 64, 128, 64, 128, 64, 128, 	64, 128, 64, 128, 64, 128, 64, 128,
//				64, 128, 64, 128, 64, 128, 64, 128, 	128, 64, 128, 64, 128, 64, 128, 64,
//				64, 128, 64, 128, 64, 128, 64, 128, 	64, 128, 64, 128, 64, 128, 64, 128,
//				64, 128, 64, 128, 64, 128, 64, 128,		128, 64, 128, 64, 128, 64, 128, 64,
//				64, 128, 64, 128, 64, 128, 64, 128, 	64, 128, 64, 128, 64, 128, 64, 128,
//				64, 128, 64, 128, 64, 128, 64, 128,		128, 64, 128, 64, 128, 64, 128, 64,
//		};
//		compressComponent(arr, 16, 32, -1);
		

		
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
