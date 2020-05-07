// Eric Shen
// 260798146

// My imports
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

public class q1 {

    // Parameters
    public static int n;
    public static int width;
    public static int height;
    public static int k;

    public static void main(String[] args) {

        try {
            // example of reading/parsing an argument
            // We need four command line argument
            if (args.length != 4) {
                throw new Exception("Missing arguments, you have " + args.length + " arguments");
            }
            else {
                //First argument is for image width
                width = Integer.parseInt(args[0]);
                //Second argument is for image height
                height = Integer.parseInt(args[1]);
                //Third argument is the number of threads
                n = Integer.parseInt(args[2]);
                //Forth argument is the number of rectangles to draw
                k = Integer.parseInt(args[3]);
            }
            // once we know what size we want we can creat an empty image
            BufferedImage outputimage = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    //I set background color to white, just for easy to read
                    outputimage.setRGB(i,j,0xFFFFFFFF);
                }
            }
            // ------------------------------------
            // Your code would go here

            // I set my timer after image initially created and before drawing rectangles
            long startTime = System.currentTimeMillis();

            // I use a if else to set single thread or multi thread
            // If it is single, just use main thread to draw
            if (n == 1) {
                for (int i = 0; i < k ; i++) {
                    draw(outputimage, setRandomRectangle());
                }
            }
            else {
                Rectangle[] allR = new Rectangle[k];    // I create a Rectangle array to keep track all rectangles threads drawing

                // This is my inner thread class
                class thread extends Thread {
                    int aID;
                    int aNumOfRectangles;

                    // I let every thread knows how many threads they need to draw
                    thread(int pID, int pNumOfRectangles) {
                        aID = pID;
                        aNumOfRectangles = pNumOfRectangles;
                    }

                    // When thread is running, it first set the rectangle then draw it
                    @Override
                    public void run() {
                        for (int i = 0; i < aNumOfRectangles; i++) {
                            try {
                                setRandomRecSynchronized(aID, allR);
                                drawSynchronizedRectangle(aID, allR, outputimage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                // Create a thread array to keep all threads we need
                thread[] threads = new thread[n];
                for (int i = 0; i < n; i++) {
                    threads[i] = new thread(i, k / n);    // every thread get equal drawing rectangle jobs first, ignore the rest which can't be divided
                }
                // for rest drawing rectangle jobs, just add them to from the first thread one by one
                for (int i = 0; i < k % n; i++) {
                    threads[i].aNumOfRectangles++;
                }

                // Initialize all threads
                for (int i = 0; i < n; i++) {
                    threads[i].start();
                }

                // join all threads to main when they finish
                for (int i = 0; i < n; i++) {
                    threads[i].join();
                }
            }
            //My timer after threads finish their drawing rectangles job
            long endTime = System.currentTimeMillis();
            System.out.println("It costs " + (endTime - startTime) + " milliseconds");
            // ------------------------------------
            // Write out the image
            File outputfile = new File("outputimage.png");
            ImageIO.write(outputimage, "png", outputfile);

        } catch (Exception e) {
            System.out.println("ERROR " +e);
            e.printStackTrace();
        }
    }

    //My helper method draw, already know the random rectangle with its size and start point
    public static void draw(BufferedImage outputImage, Rectangle y) {
        Rectangle x = y;
        int color = randomColor();
        for(int i = 0; i < x.aRectangleLength; i++){
            for(int j = 0; j < x.aRectangleWidth; j++){
                if( i == 0 || i == x.aRectangleLength -1 || j == 0 || j == x.aRectangleWidth -1) {
                    outputImage.setRGB(x.aStartPointWidth + i, x.aStartPointHeight + j, 0);
                }
                else {
                    outputImage.setRGB(x.aStartPointWidth + i, x.aStartPointHeight + j, color);
                }
            }
        }
    }

    // My helper method to draw synchronized
    public static void drawSynchronizedRectangle(int threadID, Rectangle[] rs, BufferedImage outputImage) {
        for(int i = 0; i < rs.length; i++) {
            if(threadID == i) {
                draw(outputImage, rs[i]);
            }
        }
    }

    // My helper method to set a random rectangle
    public static Rectangle setRandomRectangle() {
        int rectangleLength;
        int rectangleWidth;
        int startPointWidth;
        int startPointHeight;

        // I set the random rectangle's length and width first
        rectangleLength = ThreadLocalRandom.current().nextInt(width);
        rectangleWidth = ThreadLocalRandom.current().nextInt(height);
        // Then I choose the up-left corner as the start point, which cannot make the rectangle beyond the image
        // So I set the upper bond as the difference between image width and rectangle with. Length is the same
        startPointWidth = ThreadLocalRandom.current().nextInt(width - rectangleLength);
        startPointHeight = ThreadLocalRandom.current().nextInt(height - rectangleWidth);

        Rectangle my = new Rectangle(rectangleLength,rectangleWidth,startPointWidth,startPointHeight);
        return my;
    }

    //My helper method to get random color
    public static int randomColor() {
        float red = ThreadLocalRandom.current().nextFloat();
        float green = ThreadLocalRandom.current().nextFloat();
        float blue = ThreadLocalRandom.current().nextFloat();
        return new Color(red, green, blue).getRGB();
    }

    // My synchronized method to set random rectangles
    synchronized public static void setRandomRecSynchronized(int threadID, Rectangle[] pRectangles){
        for(int i = 0; i < pRectangles.length; i++) {
            if (threadID == i) {
                Rectangle r = setRandomRectangle();
                // If this rectangle is overlap with another rectangles
                while(drawOverlap(r, pRectangles)) {
                    r = setRandomRectangle();
                }
                pRectangles[i] = r;
            }
        }
    }

    // My helper method to check two rectangles are overlap or not
    public static boolean recOverlap( Rectangle firstRectangle, Rectangle secondRectangle) {
        return  firstRectangle.aStartPointHeight <= secondRectangle.aRectangleWidth + secondRectangle.aStartPointHeight
                && firstRectangle.aStartPointHeight + firstRectangle.aRectangleWidth >= secondRectangle.aStartPointHeight
                && firstRectangle.aStartPointWidth <= secondRectangle.aRectangleLength + secondRectangle.aStartPointWidth
                && firstRectangle.aStartPointWidth + firstRectangle.aRectangleLength >= secondRectangle.aStartPointWidth;
    }

    // My helper method to check rectangle a thread wants to draw is overlapped with other drawing rectangles or not
    public static boolean drawOverlap(Rectangle r, Rectangle[] pRectangles) {
        for (int i = 0; i< pRectangles.length; i++) {
            if (pRectangles[i] != null && recOverlap(r,pRectangles[i])) {
                return true;
            }
        }
        return false;
    }

    // My helper class to create a Rectangle known its length, width, start point width and start point height
     static class Rectangle{
        int aRectangleLength;
        int aRectangleWidth;
        int aStartPointWidth;
        int aStartPointHeight;

        public Rectangle(int pRectangleLength, int pRectangleWidth, int pStartPointWidth, int pStartPointHeight){
            aRectangleLength = pRectangleLength;
            aRectangleWidth = pRectangleWidth;
            aStartPointWidth = pStartPointWidth;
            aStartPointHeight = pStartPointHeight;
        }

    }
}
