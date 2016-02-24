package Sample;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;

import javax.swing.*;
import javax.imageio.*;

public class convertImg extends JFrame implements ActionListener{
	
	JButton m_btOpen, m_btSave, m_btConvert,
			but_default, but_high, but_recommended, but_low,
			but_preview; 
	IMGPanel	m_panelImgInput, compressed_output,
				m_panelImgOutputY, m_panelImgOutputU, m_panelImgOutputV,
				compressed_y_panel, compressed_u_panel, compressed_v_panel;
	BufferedImage 	m_imgInput, m_imgOutputY, m_imgOutputU, m_imgOutputV,
					compressed_y_buffer, compressed_u_buffer, compressed_v_buffer, compressed_output_buffer;
	static JFormattedTextField x_textfield, y_textfield;
	static JLabel width_label, height_label;
	
	//Create a file chooser
	final JFileChooser m_fc = new JFileChooser();
	
	static int picture_width = 0, picture_height = 0;
	float[] y_values, u_values, v_values, compressed_y;
	int picture_quality = 0;
	static Matrix[][] orig_mat;
    static Matrix[][] dct_mat;
    static Matrix[][] quantized_mat;
	static Matrix[][] compressed_mat;
    
    final static int window_width = 1800, window_height = 980;

    private NumberFormat number_format;
	
	//setup some GUI stuff
	public JPanel createContentPane (){	    
	    
		// We create a bottom JPanel to place everything on.
        JPanel totalGUI = new JPanel();
        totalGUI.setLayout(null);
        
        //////////////
        // DISPLAYS //
        //////////////
	    
        // ORIGINAL PICTURE
        m_panelImgInput = preparePicture(totalGUI, 10, 10, 400, 400, "Original Picture");
	    
	    // COMPRESSED PICTURE
        compressed_output = preparePicture(totalGUI, 10, 460, 400, 400, "Compressed Picture");
        
        // Y COMPONENT
        m_panelImgOutputY = preparePicture(totalGUI, 540, 10, 400, 400, "Y Component");
	    
	    // COMPRESSED Y COMPONENT
        compressed_y_panel = preparePicture(totalGUI, 540, 460, 400, 400, "Compressed Y Component");

        // U COMPONENT
        m_panelImgOutputU = preparePicture(totalGUI, 950, 10, 400, 400, "U Component");
	    
	    // COMPRESSED U COMPONENT
        compressed_u_panel = preparePicture(totalGUI, 950, 460, 400, 400, "Compressed U Component");

		// V COMPONENT
        m_panelImgOutputV = preparePicture(totalGUI, 1360, 10, 400, 400, "V Component");
	    
	    // COMPRESSED V COMPONENT
        compressed_v_panel = preparePicture(totalGUI, 1360, 460, 400, 400, "Compressed V Component");
	    
        /////////////
        // BUTTONS //
        /////////////
        
	    // create a panel for buttons
	    JPanel panelButtons = new JPanel();
	    panelButtons.setLayout(null);
	    panelButtons.setLocation(420, 50);
	    panelButtons.setSize(100, 450);
        totalGUI.add(panelButtons);

	    // create a panel for previewing 8x8 blocks
	    JPanel block_preview_panel = new JPanel();
        block_preview_panel.setBorder(BorderFactory.createLineBorder(Color.black));
	    block_preview_panel.setLayout(null);
	    block_preview_panel.setLocation(420, 550);
	    block_preview_panel.setSize(100, 300);
        totalGUI.add(block_preview_panel);
	    
	    // OPEN
	    m_btOpen = new JButton("Open");
	    prepareButton(m_btOpen, 0, 0, 100, 40);
	    m_btOpen.addActionListener(this);
	    panelButtons.add(m_btOpen);
	    
	    // SAVE
	    m_btSave = new JButton("Save");
	    prepareButton(m_btSave, 0, 50, 100, 40);
	    m_btSave.addActionListener(this);
	    panelButtons.add(m_btSave);
	    
	    // COMPRESS
	    m_btConvert = new JButton("Compress");
	    prepareButton(m_btConvert, 0, 140, 100, 60);
	    m_btConvert.addActionListener(this);
	    panelButtons.add(m_btConvert);
	    
	    // QUALITY
	    but_default = new JButton("Default Quality");
	    prepareButton(but_default, 0, 250, 100, 40);
	    but_default.addActionListener(this);
	    panelButtons.add(but_default);
	    
	    but_high = new JButton("High Quality");
	    prepareButton(but_high, 0, 300, 100, 40);
	    but_high.addActionListener(this);
	    panelButtons.add(but_high);
	    
	    but_recommended = new JButton("Recommended");
	    prepareButton(but_recommended, 0, 350, 100, 40);
	    but_recommended.addActionListener(this);
	    panelButtons.add(but_recommended);

	    but_low = new JButton("Low Quality");
	    prepareButton(but_low, 0, 400, 100, 40);
	    but_low.addActionListener(this);
	    panelButtons.add(but_low);
	    
	    // 8x8 BLOCK PREVIEWS
	    prepareTextInput();
        block_preview_panel.add(x_textfield);
        block_preview_panel.add(y_textfield);
	    prepareLabel(block_preview_panel, 0, 5, 100, 20, "INFO", 14, false);
	    prepareLabel(block_preview_panel, 0, 25, 100, 20, "Width [8x8]", 12, false);
	    prepareRefreshableLabels(block_preview_panel, 0, 45, 100, 15);
	    prepareLabel(block_preview_panel, 0, 65, 100, 20, "Height [8x8]", 12, false);
	    prepareLabel(block_preview_panel, 0, 110, 100, 20, "PREVIEW Y", 14, false);
	    prepareLabel(block_preview_panel, 0, 130, 100, 20, "X Block", 12, false);
	    prepareLabel(block_preview_panel, 0, 175, 100, 20, "Y Block", 12, false);
	    but_preview = new JButton("Preview 8x8");
	    prepareButton(but_preview, 10, 240, 80, 50);
	    but_preview.addActionListener(this);
	    block_preview_panel.add(but_preview);
	    	    
	    totalGUI.setOpaque(true);
	    return totalGUI;
	}
	
	//////////////////////////
	// PREPARE GUI ELEMENTS //
	//////////////////////////
	
	private static IMGPanel preparePicture(JPanel GUI, int xpos, int ypos, int width, int height, String label) {
        IMGPanel panel = new IMGPanel();        
        panel.setLocation(xpos, ypos);
        panel.setSize(width, height);
        panel.setBorder(BorderFactory.createLineBorder(Color.black));
	    GUI.add(panel);
	    
	    prepareLabel(GUI, xpos+20, ypos+height, width-40, 30, label, 16, false);
	    
	    return panel;
	}
	
	private static void prepareLabel(JPanel GUI, int xpos, int ypos, int width, int height, String label, int textsize, boolean border) {
		JLabel input_label = new JLabel();
	    input_label.setText(label);
	    input_label.setHorizontalAlignment(SwingConstants.CENTER);
	    input_label.setVerticalAlignment(SwingConstants.CENTER);
	    input_label.setBounds(xpos, ypos, width, height);
	    input_label.setFont(new Font("Verdana", 1, textsize));
	    if (border) input_label.setBorder(BorderFactory.createLineBorder(Color.black));
	    GUI.add(input_label);
	}
	
	private static void prepareRefreshableLabels(JPanel GUI, int xpos, int ypos, int width, int height) {
		width_label = new JLabel();
		width_label.setText("" + picture_width + " [" + (int)Math.ceil(picture_width/8) + "]");
		width_label.setHorizontalAlignment(SwingConstants.CENTER);
		width_label.setVerticalAlignment(SwingConstants.CENTER);
		width_label.setBounds(xpos, ypos, width, height);
		width_label.setFont(new Font("Verdana", 1, 10));
	    GUI.add(width_label);
	    
		height_label = new JLabel();
		height_label.setText("" + picture_height + " [" + (int)Math.ceil(picture_height/8) + "]");
		height_label.setHorizontalAlignment(SwingConstants.CENTER);
		height_label.setVerticalAlignment(SwingConstants.CENTER);
		height_label.setBounds(xpos, ypos+40, width, height);
		height_label.setFont(new Font("Verdana", 1, 10));
	    GUI.add(height_label);
	}
	
	private static void refreshLabels() {
		width_label.setText("" + picture_width + " [" + (int)Math.ceil(picture_width/8) + "]");
		height_label.setText("" + picture_height + " [" + (int)Math.ceil(picture_height/8) + "]");
	}
	
	private void prepareButton(JButton button, int xpos, int ypos, int width, int height) {
		button.setMargin(new Insets(0,0,0,0));
		button.setFont(new Font("Arial", Font.BOLD, 12));
		button.setLocation(xpos, ypos);
		button.setSize(width, height);
	}
	
	private void prepareTextInput() {
        number_format = NumberFormat.getNumberInstance();
        number_format.setMaximumIntegerDigits(5);
        x_textfield = new JFormattedTextField(number_format);
        x_textfield.setColumns(5);
        x_textfield.setLocation(10, 150);
        x_textfield.setSize(80, 25);
        y_textfield = new JFormattedTextField(number_format);
        y_textfield.setColumns(5);
        y_textfield.setLocation(10, 195);
        y_textfield.setSize(80, 25);
	}
	
	/////////////////////
	// CONVERSION CODE //
	/////////////////////
	
	private void conversion() {
		if(m_imgInput == null)
    		return;
    	int w = m_imgInput.getWidth(null);
    	int h = m_imgInput.getHeight(null);
    	
    	picture_width = w;
    	picture_height = h;

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
        
        // determine the quality we will use
        int y_quality, uv_quality;
        switch (picture_quality) {
        	case 1:
        		y_quality = 3;
        		uv_quality = 3;
        		break;
        	case 2:
        		y_quality = 1;
        		uv_quality = 2;
        		break;
        	case 3:
        		y_quality = 4;
        		uv_quality = 4;
        		break;
        	default:
        		y_quality = -1;
        		uv_quality = -1;
        }
        
        // use JPEG compression
        compressed_y = compressComponent(y_values, w, h, y_quality, true);
        float[] compressed_u = compressComponent(u_values_sub, w_sub, h_sub, uv_quality, false);
        float[] compressed_v = compressComponent(v_values_sub, w_sub, h_sub, uv_quality, false);
        
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

            // normalize values, otherwise we'll get odd colours
            if (y_values[index] < 0) y_values[index] = 0;
            else if (y_values[index] > 255) y_values[index] = 255;
            if (u_values[index] < 0) u_values[index] = 0;
            else if (u_values[index] > 255) u_values[index] = 255;
            if (v_values[index] < 0) v_values[index] = 0;
            else if(v_values[index] > 255) v_values[index] = 255;
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
	private static float[] compressComponent(float[] arr, int w, int h, int quality, boolean save) {
        Matrix[][] mat = Matrix.array1DTo2DMatrix(arr, w, h);
        Matrix[][] dct = new Matrix[h][w];
        Matrix[][] quantized = new Matrix[h][w];
        Matrix temp;
        
        if (save)
        	orig_mat = mat;
        
        for (int i = 0; i < Math.ceil((double)h/8); i++) {
        	for (int j = 0; j < Math.ceil((double)w/8); j++) {
        		dct[i][j] = Transform.dctransform(mat[i][j]);
        		temp = Quantization.quantize(dct[i][j], quality);
        		quantized[i][j] = Quantization.inv_quantize(temp, quality);
        		mat[i][j] = Transform.inv_dctransform(quantized[i][j]);
        		mat[i][j].normalizeValues();
        	}
        }
        
        if (save) {
        	dct_mat = dct;
        	quantized_mat = quantized;
        	compressed_mat = mat;
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
	
	//////////////////////////
	// PREVIEWING 8x8 BLOCK //
	//////////////////////////
	
	private static void createPreview(int parsed_x, int parsed_y) {
    	if (parsed_x < 0 || parsed_y < 0)
    		return;
    	
        JFrame frame = new JFrame("Preview of block (" + parsed_x + ", " + parsed_y + ")");
        frame.setLayout(null);
        
        frame.setContentPane(preparePreview(parsed_x, parsed_y));
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1250, 350);
        frame.setVisible(true);
    }
    
    private static JPanel preparePreview(int parsed_x, int parsed_y) {
    	JPanel panel = new JPanel();
    	JPanel element;
    	
    	element = preview2DMatrixElement(orig_mat, 5, 5, parsed_x, parsed_y, "Original Y Values");
    	panel.add(element);
    	element = preview2DMatrixElement(dct_mat, 315, 5, parsed_x, parsed_y, "Raw DCT Coefficients");
    	panel.add(element);
    	element = preview2DMatrixElement(quantized_mat, 625, 5, parsed_x, parsed_y, "Quantized DCT Coefficients");
    	panel.add(element);
    	element = preview2DMatrixElement(compressed_mat, 930, 5, parsed_x, parsed_y, "Original Y Values");
    	panel.add(element);
    	
	    panel.setOpaque(true);
    	
    	return panel;
    }
    
    private static JPanel preview2DMatrixElement(Matrix[][] mat, int x_pos, int y_pos, int x, int y, String label) {
    	JPanel mat_panel = new JPanel();
	    mat_panel.setLayout(null);
	    mat_panel.setLocation(x_pos, y_pos);
	    mat_panel.setSize(300, 320);
	    mat_panel.setBorder(BorderFactory.createLineBorder(Color.black));
	    
	    prepareLabel(mat_panel, 0, 0, 300, 20, label, 20, false);

	    float[] arr = mat[y][x].matrix1Dto1DArray();
	    
	    for (int i = 0; i < 64; i++) {
	    	int xx = i % 8;
	    	int yy = i / 8;
	    	prepareLabel(mat_panel, 10 + (xx*35), 10 + (yy*35), 35, 35, "" + arr[i], 12, true);
	    }
	    
        return mat_panel;
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
        	refreshLabels();
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
    	
    	// QUALITY SETTINGS
        else if (evnt.getSource() == but_default)
        	picture_quality = 0;
        else if (evnt.getSource() == but_high)
			picture_quality = 1;
        else if (evnt.getSource() == but_recommended)
        	picture_quality = 2;
        else if (evnt.getSource() == but_low)
        	picture_quality = 3;
    	
    	// PREVIEW 8x8 BLOCK
        else if (evnt.getSource() == but_preview) {
        	String parsed_x = x_textfield.getText();
        	String parsed_y = y_textfield.getText();
        	if (picture_width <= 0 || picture_height <= 0)
        		return;
        	if (parsed_x == null || parsed_y == null || parsed_x.equals("") || parsed_y.equals(""))
        		return;
        	createPreview(Integer.parseInt(parsed_x), Integer.parseInt(parsed_y));
        }
    }
	
    private static void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(false);
        JFrame frame = new JFrame("JPEG Compression");

        //Create and set up the content pane.
        convertImg demo = new convertImg();
        frame.setContentPane(demo.createContentPane());
        frame.setSize(window_width, window_height);
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
