import java.awt.*;
import javax.swing.*;

 class Main  {
    public static void main(String[] args) {
        JFrame window = new JFrame("Checkers");
        Checker content = new Checker();
        window.setContentPane(content);
        window.pack();
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation( (screensize.width - window.getWidth())/2,
                (screensize.height - window.getHeight())/2 );
        window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        window.setResizable(false);
        window.setVisible(true);
    }




}