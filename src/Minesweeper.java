import  java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import javax.swing.*;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.File;

public class Minesweeper {

    public class MineTile extends JButton{
        int r,c;

        public MineTile(int r,int c){
            this.r=r;
            this.c=c;
        }
    }

    int tileSize=70;
    int numRows=8;
    int numCols=numRows;
    int boardWidth =numCols*tileSize;
    int boardHeight=numRows*tileSize;

    JFrame frame = new JFrame("Minesweeper");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel =new JPanel();

    int mineCount = 10;
    MineTile[][] board = new MineTile[numRows][numCols];
    ArrayList<MineTile> mineList;
    Random random = new Random();

    int tilesClicked=0;
    boolean gameOver=false;

    Minesweeper() {
//        frame.setVisible(true);
        frame.setSize(boardWidth,boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font ("Arial",Font.BOLD,25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
//        textLabel.setText(("Minesweeper"));
        textLabel.setText(("Minesweeper: "+ Integer.toString(mineCount)));
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel,BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(numRows,numCols));
        boardPanel.setBackground(Color.GREEN);
        frame.add(boardPanel);

        for(int r=0;r<numRows;r++){
            for(int c=0;c<numCols;c++){
             MineTile tile = new MineTile(r,c);
             board[r][c] =tile;

             tile.setFocusable(false);
             tile.setMargin(new Insets(0,0,0,0));
             tile.setFont(new Font("Arial Unicode MS",Font.PLAIN,45));
//             tile.setText("💣");
                //im gonna change something here
             tile.addMouseListener(new MouseAdapter() {
                 @Override
                 public void mousePressed(MouseEvent e){
                     if(gameOver){
                         return;
                     }
                     MineTile tile=(MineTile) e.getSource();

                     //left click
                     if(e.getButton() == MouseEvent.BUTTON1){
                         playSound("src/assets/tap-notification-180637.wav");
                        if(Objects.equals(tile.getText(), "")){
                            if(mineList.contains(tile)){
                                revealMines();
                            }
                            else{
                                checkMIne(tile.r,tile.c);
                            }
                        }
                     }
                     else if (e.getButton()==MouseEvent.BUTTON3){
                         if(Objects.equals(tile.getText(), "") && tile.isEnabled()){
                             tile.setText(("🚩"));
                         }
                         else if(Objects.equals(tile.getText(), "🚩")){
                             tile.setText("");
                         }
                     }
                 }
             });
             boardPanel.add(tile);
            }
        }

        frame.setVisible(true);

        setMines();
    }

//    private void playBackgroundMusic() {
//        try {
//            // Load a background music file (adjust the path as needed)
//            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("src/assets/1-03. Professor Oak.wav")); //use wav only
//            backgroundMusicClip = AudioSystem.getClip();
//            backgroundMusicClip.open(audioStream);
//            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY); // Play the sound in a loop
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Error loading or playing background music.");
//        }
//    }

    void setMines(){
        mineList=new ArrayList<MineTile>();
        int mineLeft = mineCount;
        while(mineLeft>0){
            int r= random.nextInt(numRows);
            int c=random.nextInt(numCols);

            MineTile tile = board[r][c];
            if(!mineList.contains(tile)){
                mineList.add(tile);
                mineLeft-=1;
            }
        }

    }


    void revealMines(){
        //did a bit pakami in this function.
        for (MineTile tile : mineList) {
            tile.setText("💣");
        }
        playSound("src/assets/explosion.wav");
        gameOver=true;
        textLabel.setText("Game Over");
    }

    private void playSound(String soundFile) {
        try {
            File file = new File(soundFile);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }
    void checkMIne(int r, int c){
        if(r<0 ||r>=numRows||c<0||c>=numCols){
            return;
        }
        MineTile tile = board[r][c];
        if (!tile.isEnabled()){
            return;
        }
        tile.setEnabled(false);
        tilesClicked+=1;
        int minesFound=0;

        //top3
        minesFound +=countMine(r-1,c-1); //top left
        minesFound +=countMine(r-1,c); //top
        minesFound +=countMine(r-1,c+1); //top right
        minesFound +=countMine(r,c-1);//left
        minesFound +=countMine(r,c+1);//right
        minesFound +=countMine(r+1,c-1);//bottom left
        minesFound +=countMine(r+1,c);//bottom
        minesFound +=countMine(r+1,c+1);//bottom right

        if (minesFound >0){
            tile.setText(Integer.toString(minesFound));
        }
        else{
            tile.setText("");
            //top3
            checkMIne(r-1,c-1);
            checkMIne(r-1,c);
            checkMIne(r-1,c+1);
            checkMIne(r,c-1);
            checkMIne(r,c+1);
            checkMIne(r+1,c-1);
            checkMIne(r+1,c);
            checkMIne(r+1,c+1);
        }

        if (tilesClicked == numRows * numCols -mineList.size()){
            gameOver=true;
            textLabel.setText("Mines Cleared");
        }
    }

    int countMine(int r,int c){
        if(r<0 ||r>=numRows||c<0||c>=numCols){
            return 0;
        }
        if(mineList.contains(board[r][c])){
            return 1;
        }
        return 0;
    }
}
