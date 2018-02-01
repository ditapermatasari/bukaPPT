/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bukappt;

import com.sun.image.codec.jpeg.ImageFormatException;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author Novia
 */
public class OpenPPt extends javax.swing.JFrame {
    /* Flags and sizes */
	public static int HEADER_SIZE = 8;
	public static int MAX_PACKETS = 500;
	public static int SESSION_START = 128;
	public static int SESSION_END = 64;
	public static int DATAGRAM_MAX_SIZE = 65507 - HEADER_SIZE;
	public static int MAX_SESSION_NUMBER = 255;

	/*
	 * The absolute maximum datagram packet size is 65507, The maximum IP packet
	 * size of 65535 minus 20 bytes for the IP header and 8 bytes for the UDP
	 * header.
	 */
	public static String OUTPUT_FORMAT = "jpg";

	public static int COLOUR_OUTPUT = BufferedImage.TYPE_INT_RGB;

	/* Default parameters */
	public static double SCALING = 0.5;
	public static int SLEEP_MILLIS = 2000;
	//public static String IP_ADDRESS =  "225.4.5.6";
        public static String IP_ADDRESS =  "239.255.255.250";
	public static int PORT = 5000;
	public static boolean SHOW_MOUSEPOINTER = true;

	/**
	 * Takes a screenshot (fullscreen)
	 * 
	 * @return Sreenshot
	 * @throws AWTException
	 * @throws ImageFormatException
	 * @throws IOException
	 */
	public static BufferedImage getScreenshot() throws AWTException,
			ImageFormatException, IOException {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		Rectangle screenRect = new Rectangle(screenSize);

		Robot robot = new Robot();
		BufferedImage image = robot.createScreenCapture(screenRect);
               

		return image;
	}

	/**
	 * Returns a random image from given directory
	 * 
	 * @param dir Image directory
	 * @return Random Image
	 * @throws IOException
	 */
	public static BufferedImage getRandomImageFromDir(File dir) throws IOException {
		String[] images = dir.list(new ImageFileFilter());
        int random = new Random().nextInt(images.length);

        String fileName = dir.getAbsoluteFile() + File.separator + images[random];
        File imageFile = new File(fileName);

		return ImageIO.read(imageFile);
	}

	/**
	 * Converts BufferedImage to byte array
	 * 
	 * @param image Image to convert
	 * @param format Image format (JPEG, PNG or GIF)
	 * @return Byte Array
	 * @throws IOException
	 */
	public static byte[] bufferedImageToByteArray(BufferedImage image, String format) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, format, baos);
		return baos.toByteArray();
	}

	/**
	 * Scales a bufferd image 
	 * 
	 * @param source Image to scale
	 * @param w Image widht
	 * @param h Image height
	 * @return Scaled image
	 */
	public static BufferedImage scale(BufferedImage source, int w, int h) {
		Image image = source
				.getScaledInstance(w, h, Image.SCALE_AREA_AVERAGING);
		BufferedImage result = new BufferedImage(w, h, COLOUR_OUTPUT);
		Graphics2D g = result.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return result;
	}

	/**
	 * Shrinks a BufferedImage
	 * 
	 * @param source Image to shrink
	 * @param factor Scaling factor
	 * @return Scaled image
	 */
	public static BufferedImage shrink(BufferedImage source, double factor) {
		int w = (int) (source.getWidth() * factor);
		int h = (int) (source.getHeight() * factor);
		return scale(source, w, h);
	}

	/**
	 * Copies a BufferedImage
	 * 
	 * @param image Image to copy
	 * @return Copied image
	 */
	public static BufferedImage copyBufferedImage(BufferedImage image) {
		BufferedImage copyOfIm = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g = copyOfIm.createGraphics();
        g.drawRenderedImage(image, null);
        g.dispose();
        return copyOfIm;
    }

	/*
	
	 */
	/**
	 * Sends a byte array via multicast
	 * Multicast addresses are IP addresses in the range of 224.0.0.0 to
	 * 239.255.255.255.
	 * 
	 * @param imageData Byte array
	 * @param multicastAddress IP multicast address
	 * @param port Port
	 * @return <code>true</code> on success otherwise <code>false</code>
	 */
	private boolean sendImage(byte[] imageData, String multicastAddress,
			int port) {
		InetAddress ia;

		boolean ret = false;
		int ttl = 205;

		try {
			ia = InetAddress.getByName(multicastAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return ret;
		}

		MulticastSocket ms = null;

		try {
			ms = new MulticastSocket();
			ms.setTimeToLive(ttl);
			DatagramPacket dp = new DatagramPacket(imageData, imageData.length,
					ia, port);
			ms.send(dp);
			ret = true;
		} catch (IOException e) {
			e.printStackTrace();
			ret = false;
		} finally {
			if (ms != null) {
					ms.close();
			}
		}

		return ret;
	}

    /**
     * Creates new form OpenPPt
     */
    private final JFileChooser openFileChooser;
    public OpenPPt() {
        initComponents();
        openFileChooser = new JFileChooser();
        openFileChooser.setCurrentDirectory(new File("C:\\temp"));
        
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jBrowser = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("Buka PPT");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jBrowser.setText("Browser PPT");
        jBrowser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBrowserActionPerformed(evt);
            }
        });

        jButton2.setText("ScreenShoot");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(29, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jBrowser)
                        .addGap(85, 85, 85))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addGap(62, 62, 62))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jBrowser))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2))))
                .addContainerGap(155, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
         // TODO add your handling code here:
       try 
       {
            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) 
            {
              desktop = Desktop.getDesktop();
            }

            //desktop.open(new File("D:\\KULIAH\\SEMESTER 5\\KEAMANAN JARINGAN\\StromWorm2007.pptx"));
            desktop.open(new File("D:\\A\\Rek creative media1.pptx"));
        } 
       catch (IOException ioe) 
        {
            ioe.printStackTrace();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jBrowserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBrowserActionPerformed
       
        int retrunValue = openFileChooser.showOpenDialog(this);
        if (retrunValue == JFileChooser.APPROVE_OPTION)
        {
            
            try 
       {
            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) 
            {
              desktop = Desktop.getDesktop();
            }

            desktop.open(new File(""+openFileChooser.getSelectedFile()));
            
            OpenPPt sender = new OpenPPt();
		int sessionNumber = 0;
		boolean multicastImages = false;
		
		
		// Create Frame
		JFrame frame = new JFrame("Multicast Image Sender");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel label = new JLabel();
		frame.getContentPane().add(label);
		frame.setVisible(true);

		/* Check weather to multicast screenshots or images */
		if(new File("images").exists() && new File("images").isDirectory()) {
			label.setText("Multicasting images...");
			multicastImages = true;
		}
		else {
			label.setText("Multicasting screenshots...");
		}
		
		frame.pack();

		try {
			/* Continuously send images */
			while (true) {
				BufferedImage image;

				/* Get image or screenshot */
				if(multicastImages) {
					image = getRandomImageFromDir(new File("images"));
				}
				else {
					image = getScreenshot();
					
					/* Draw mousepointer into image */
					if(SHOW_MOUSEPOINTER) {
						 PointerInfo p = MouseInfo.getPointerInfo();
						 int mouseX = p.getLocation().x;
						 int mouseY = p.getLocation().y;
						 
						 Graphics2D  g2d = image.createGraphics();
						 g2d.setColor(Color.red);
						 Polygon polygon1 = new Polygon(new int[] { mouseX, mouseX+10, mouseX, mouseX},
						                                       new int[] { mouseY, mouseY+10, mouseY+15, mouseY}
						          , 4);
						 
						 Polygon polygon2 = new Polygon(new int[] { mouseX+1, mouseX+10+1, mouseX+1, mouseX+1},
	                             new int[] { mouseY+1, mouseY+10+1, mouseY+15+1, mouseY+1}
	, 4);
						 g2d.setColor(Color.black);
						 g2d.fill(polygon1);
						 
						 g2d.setColor(Color.red);
						 g2d.fill(polygon2);
						 g2d.dispose();
					}
					 
					 
				}

				/* Scale image */
				image = shrink(image, SCALING);
				byte[] imageByteArray = bufferedImageToByteArray(image, OUTPUT_FORMAT);
				int packets = (int) Math.ceil(imageByteArray.length / (float)DATAGRAM_MAX_SIZE);

				/* If image has more than MAX_PACKETS slices -> error */
				if(packets > MAX_PACKETS) {
					System.out.println("Image is too large to be transmitted!");
					continue;
				}

				/* Loop through slices */
				for(int i = 0; i <= packets; i++) {
					int flags = 0;
					flags = i == 0 ? flags | SESSION_START: flags;
					flags = (i + 1) * DATAGRAM_MAX_SIZE > imageByteArray.length ? flags | SESSION_END : flags;

					int size = (flags & SESSION_END) != SESSION_END ? DATAGRAM_MAX_SIZE : imageByteArray.length - i * DATAGRAM_MAX_SIZE;

					/* Set additional header */
					byte[] data = new byte[HEADER_SIZE + size];
					data[0] = (byte)flags;
					data[1] = (byte)sessionNumber;
					data[2] = (byte)packets;
					data[3] = (byte)(DATAGRAM_MAX_SIZE >> 8);
					data[4] = (byte)DATAGRAM_MAX_SIZE;
					data[5] = (byte)i;
					data[6] = (byte)(size >> 8);
					data[7] = (byte)size;

					/* Copy current slice to byte array */
					System.arraycopy(imageByteArray, i * DATAGRAM_MAX_SIZE, data, HEADER_SIZE, size);
					/* Send multicast packet */
					sender.sendImage(data, IP_ADDRESS, PORT);

					/* Leave loop if last slice has been sent */
					if((flags & SESSION_END) == SESSION_END) break;
				}
				/* Sleep */
				Thread.sleep(SLEEP_MILLIS);
				
				/* Increase session number */
				sessionNumber = sessionNumber < MAX_SESSION_NUMBER ? ++sessionNumber : 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        } 
       catch (IOException ioe) 
        {
            ioe.printStackTrace();
        }
           File file = openFileChooser.getSelectedFile();

        } 
        else 
        {
          
        }
    }//GEN-LAST:event_jBrowserActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
//         try {
//            BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
//            ImageIO.write(image, "png", new File("F:\\dhini\\j.jpg"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

           try {
            Robot r = new Robot();
            Rectangle rec = new Rectangle( Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage img = r.createScreenCapture(rec);
            ImageIO.write(img, "jpg", new File("F:\\dhini\\km.jpg"));
            
        } catch (Exception e) {
        }

        
    }//GEN-LAST:event_jButton2ActionPerformed

    public static void printScreen(String imagename)
    {
        try {
            Robot r = new Robot();
            Rectangle rec = new Rectangle( Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage img = r.createScreenCapture(rec);
            ImageIO.write(img, "jpg", new File(imagename + ".jpg"));
            
        } catch (Exception e) {
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(OpenPPt.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(OpenPPt.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(OpenPPt.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(OpenPPt.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OpenPPt().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBrowser;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    // End of variables declaration//GEN-END:variables
}
class ImageFileFilter implements FilenameFilter
{
    /* (non-Javadoc)
     * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
     */
    public boolean accept( File dir, String name )
    {
      String nameLc = name.toLowerCase();
      return nameLc.endsWith(".jpg") ? true : false;
    }
}