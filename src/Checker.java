import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.ArrayList;

public class Checker extends JPanel {
    private JButton newGameButton;  // Button for starting a new game.
    private JButton resignButton;   // Button that a player can use to end
    //    the game by resigning.
    private JButton saveButton;     // Button for saving the state of a game.
    private JButton loadButton;     // Button for loading a saved game back
    //   into the program.

    private JLabel message;  // Label for displaying messages to the user.
    private JLabel Rmessage;  // Label for displaying messages to the user.
    private JLabel Bmessage;  // Label for displaying messages to the user.

    public Checker() {

        setLayout(null);  // I will do the layout myself.
        setPreferredSize( new Dimension(450,350) );

        setBackground(new Color(55, 29, 16));  // Dark green background.


        Board board = new Board();  // Note: The constructor for the

        add(board);
        add(newGameButton);
        add(resignButton);
        add(saveButton);
        add(loadButton);
        add(message);
        add(Rmessage);
        add(Bmessage);


        board.setBounds(20,20,264,264); // Note:  size MUST be 164-by-164 !
        newGameButton.setBounds(310, 20, 120, 30);
        loadButton.setBounds(310, 65, 120, 30);
        saveButton.setBounds(310, 110, 120, 30);
        resignButton.setBounds(310, 155, 120, 30);
        message.setBounds(0, 300, 350, 30);
        Rmessage.setBounds(200, 210, 350, 30);
        Bmessage.setBounds(200, 250, 350, 30);

    } // end constructor






    private class Board extends JPanel implements ActionListener, MouseListener {


        CheckersData board;  // The data for the checkers board is kept here.

        boolean gameInProgress; // Is a game currently in progress?


        int currentPlayer;

        int selectedRow, selectedCol;  // If the current player has selected a piece to

        CheckersMove[] legalMoves;  // An array containing the legal moves for the


        public void mouseReleased(MouseEvent evt) { }
        public void mouseClicked(MouseEvent evt) { }
        public void mouseEntered(MouseEvent evt) { }
        public void mouseExited(MouseEvent evt) { }

        Board() {
            setBackground(Color.BLACK);
            addMouseListener(this);
            resignButton = new JButton("Resign");
            resignButton.addActionListener(this);
            newGameButton = new JButton("New Game");
            newGameButton.addActionListener(this);
            saveButton = new JButton("Save Game");
            saveButton.addActionListener(this);
            loadButton = new JButton("Load Game");
            loadButton.addActionListener(this);
            message = new JLabel("",JLabel.CENTER);
            Rmessage = new JLabel("White score: 0",JLabel.CENTER);
            Bmessage = new JLabel("Black score: 0",JLabel.CENTER);
            message.setFont(new  Font("Serif", Font.BOLD, 14));
            message.setForeground(Color.CYAN);

            Rmessage.setFont(new  Font("Serif", Font.BOLD, 20));
            Rmessage.setForeground(Color.WHITE);
            Bmessage.setFont(new  Font("Serif", Font.BOLD, 20));
            Bmessage.setForeground(Color.WHITE);
            board = new CheckersData();
            doNewGame();
        }



        public void actionPerformed(ActionEvent evt) {
            Object src = evt.getSource();
            if (src == newGameButton)
                doNewGame();
            else if (src == resignButton)
                doResign();
            else if (src == saveButton)
                doSave();
            else if (src == loadButton)
                doLoad();
        }


        void doSave() {

            File selectedFile;  // Initially selected file name in the dialog.
            selectedFile = new File("CheckersGame.txt");

            PrintWriter out;
            try {
                FileWriter stream = new FileWriter(selectedFile);
                out = new PrintWriter( stream );
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Sorry, but an error occurred while trying to open the file:\n" + e);
                return;
            }
            try {
                out.println("CheckersWithFiles"); // Identifies file as a Checkers game.
                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 8; col++) {
                        int piece = board.pieceAt(row, col);
                        if (piece == CheckersData.EMPTY) {
                            out.print('.');
                        } else if (piece == CheckersData.RED) {
                            out.print('r');
                        } else if (piece == CheckersData.BLACK) {
                            out.print('b');
                        } else if (piece == CheckersData.RED_KING) {
                            out.print('R');
                        } else if (piece == CheckersData.BLACK_KING) {
                            out.print('B');
                        }
                    }
                    out.println();
                }

                out.println(currentPlayer == CheckersData.RED ? "Red" : "Black");
                out.flush();
                out.close();
                if (out.checkError())
                    throw new IOException("Some error occurred while saving the file.");
                else
                {
                    JOptionPane.showMessageDialog(this,
                            "Successfully saved file\n");
                }
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Sorry, but an error occurred while trying to write the text:\n" + e);
            }
        }



        void doLoad() {

            File selectedFile = new File("CheckersGame.txt");

            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                CheckersData newBoard = new CheckersData();
                int newCurrentPlayer=1;
                String programNameFromFile = reader.readLine();

                if (!programNameFromFile.equals("CheckersWithFiles")) {
                    throw new IOException("Selected file does not contain a checkers game.");
                }

                for (int row = 0; row < 8; row++) {
                    String rowContent = reader.readLine();
                    for (int col = 0; col < 8; col++) {
                        char pieceCode = rowContent.charAt(col);
                        if (pieceCode == '.') {
                            newBoard.setPieceAt(row, col, CheckersData.EMPTY);
                        } else if (pieceCode == 'r') {
                            newBoard.setPieceAt(row, col, CheckersData.RED);
                        } else if (pieceCode == 'b') {
                            newBoard.setPieceAt(row, col, CheckersData.BLACK);
                        } else if (pieceCode == 'R') {
                            newBoard.setPieceAt(row, col, CheckersData.RED_KING);
                        } else if (pieceCode == 'B') {
                            newBoard.setPieceAt(row, col, CheckersData.BLACK_KING);
                        } else {
                            throw new IOException("Illegal board data found in file.");
                        }
                        if (row % 2 != col % 2 && newBoard.pieceAt(row, col) != CheckersData.EMPTY) {
                            throw new IOException("Illegal board data found in file.");
                        }
                    }
                }

                String currentPlayerString = reader.readLine();
                if (currentPlayerString.equals("Red"))
                    newCurrentPlayer = CheckersData.RED;
                else if (currentPlayerString.equals("Black"))
                    newCurrentPlayer = CheckersData.BLACK;

                board = newBoard;  // Set up game with data read from file.
                currentPlayer = newCurrentPlayer;
                legalMoves = board.getLegalMoves(currentPlayer);
                selectedRow = -1;
                gameInProgress = true;
                newGameButton.setEnabled(false);
                loadButton.setEnabled(false);
                saveButton.setEnabled(true);
                resignButton.setEnabled(true);
                if (currentPlayer == CheckersData.RED)
                    message.setText("Game loaded -- it's RED's move.");
                else
                    message.setText("Game loaded -- it's BLACK's move.");
                repaint();
                Rmessage.setText("White score: "+(12-board.countPiece(CheckersData.BLACK)));
                Bmessage.setText("Black score: "+(12-board.countPiece(CheckersData.RED)));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Sorry, but an error occurred while trying to open the file:\n" + e);            }

        }



        void doNewGame() {
            if (gameInProgress == true) {
                // This should not be possible, but it doesn't hurt to check.
                message.setText("Finish the current game first!");
                return;
            }
            board.setUpGame();   // Set up the pieces.
            currentPlayer = CheckersData.RED;   // RED moves first.
            legalMoves = board.getLegalMoves(CheckersData.RED);  // Get RED's legal moves.
            selectedRow = -1;   // RED has not yet selected a piece to move.
            message.setText("White:  Make your move.");
            gameInProgress = true;
            newGameButton.setEnabled(false);
            loadButton.setEnabled(false);
            saveButton.setEnabled(true);
            resignButton.setEnabled(true);
            Rmessage.setText("White score: "+(12-board.countPiece(CheckersData.BLACK)));
            Bmessage.setText("Black score: "+(12-board.countPiece(CheckersData.RED)));
            repaint();
        }


        void doResign() {
            if (gameInProgress == false) {
                message.setText("There is no game in progress!");
                return;
            }
            if (currentPlayer == CheckersData.RED)
                gameOver("White resigns.  BLACK wins.");
            else
                gameOver("BLACK resigns.  RED wins.");
        }



        void gameOver(String str) {
            message.setText(str);
            newGameButton.setEnabled(true);
            loadButton.setEnabled(true);
            saveButton.setEnabled(false);
            resignButton.setEnabled(false);
            gameInProgress = false;
        }



        void doClickSquare(int row, int col) {

            for (int i = 0; i < legalMoves.length; i++)
                if (legalMoves[i].fromRow == row && legalMoves[i].fromCol == col) {
                    selectedRow = row;
                    selectedCol = col;
                    if (currentPlayer == CheckersData.RED)
                        message.setText("White:  Make your move.");
                    else
                        message.setText("BLACK:  Make your move.");
                    repaint();
                    return;
                }

            if (selectedRow < 0) {
                message.setText("Click the piece you want to move.");
                return;
            }



            for (int i = 0; i < legalMoves.length; i++)
                if (legalMoves[i].fromRow == selectedRow && legalMoves[i].fromCol == selectedCol
                        && legalMoves[i].toRow == row && legalMoves[i].toCol == col) {
                    doMakeMove(legalMoves[i]);
                    return;
                }


            message.setText("Click the square you want to move to.");

        }  // end doClickSquare()


        void doMakeMove(CheckersMove move) {

            board.makeMove(move);
            Rmessage.setText("White score: "+(12-board.countPiece(CheckersData.BLACK)));
            Bmessage.setText("Black score: "+(12-board.countPiece(CheckersData.RED)));


            if (move.isJump()) {
                legalMoves = board.getLegalJumpsFrom(currentPlayer,move.toRow,move.toCol);
                if (legalMoves != null) {
                    if (currentPlayer == CheckersData.RED)
                        message.setText("White:  You must continue jumping.");
                    else
                        message.setText("BLACK:  You must continue jumping.");
                    selectedRow = move.toRow;  // Since only one piece can be moved, select it.
                    selectedCol = move.toCol;
                    repaint();
                    return;
                }
            }


            if (currentPlayer == CheckersData.RED) {
                currentPlayer = CheckersData.BLACK;
                legalMoves = board.getLegalMoves(currentPlayer);
                if (legalMoves == null)
                    gameOver("BLACK has no moves.  RED wins.");
                else if (legalMoves[0].isJump())
                    message.setText("BLACK:  Make your move.  You must jump.");
                else
                    message.setText("BLACK:  Make your move.");
            }
            else {
                currentPlayer = CheckersData.RED;
                legalMoves = board.getLegalMoves(currentPlayer);
                if (legalMoves == null)
                    gameOver("White has no moves.  BLACK wins.");
                else if (legalMoves[0].isJump())
                    message.setText("White:  Make your move.  You must jump.");
                else
                    message.setText("White:  Make your move.");
            }


            selectedRow = -1;


            if (legalMoves != null) {
                boolean sameStartSquare = true;
                for (int i = 1; i < legalMoves.length; i++)
                    if (legalMoves[i].fromRow != legalMoves[0].fromRow
                            || legalMoves[i].fromCol != legalMoves[0].fromCol) {
                        sameStartSquare = false;
                        break;
                    }
                if (sameStartSquare) {
                    selectedRow = legalMoves[0].fromRow;
                    selectedCol = legalMoves[0].fromCol;
                }
            }


            repaint();

        }  // end doMakeMove();



        public void paintComponent(Graphics g) {


            g.setColor(Color.black);
            g.drawRect(0,0,getSize().width-1,getSize().height-1);
            g.drawRect(1,1,getSize().width-3,getSize().height-3);

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (row % 2 == col % 2) {
                        g.setColor(new Color(181, 101, 29));
                    } else {
                        g.setColor(Color.LIGHT_GRAY);
                    }

                    g.fillRect(2 + col * 33, 2 + row * 33, 120, 120);

                    int piece = board.pieceAt(row, col);

                    if (piece == CheckersData.RED) {
                        g.setColor(Color.WHITE);
                        g.fillOval(4 + col * 33, 4 + row * 33, 25, 25);
                    } else if (piece == CheckersData.BLACK) {
                        g.setColor(Color.BLACK);
                        g.fillOval(4 + col * 33, 4 + row * 33, 25, 25);
                    } else if (piece == CheckersData.RED_KING) {
                        g.setColor(Color.WHITE);
                        g.fillOval(4 + col * 33, 4 + row * 33, 25, 25);
                        g.setColor(Color.RED);
                        Font boldFont = new Font("Arial", Font.BOLD, 16);
                        g.setFont(boldFont);
                        g.drawString("K", 12 + col * 33, 24 + row * 33);
                    } else if (piece == CheckersData.BLACK_KING) {
                        g.setColor(Color.BLACK);
                        g.fillOval(4 + col * 33, 4 + row * 33, 25, 25);
                        g.setColor(Color.RED);
                        Font boldFont = new Font("Arial", Font.BOLD, 16);
                        g.setFont(boldFont);
                        g.drawString("K", 12 + col * 33, 24 + row * 33);
                    }
                }
            }

            if (gameInProgress) {
                /* First, draw a 2-pixel cyan border around the pieces that can be moved. */
                g.setColor(Color.cyan);
                for (int i = 0; i < legalMoves.length; i++) {
                    g.drawRect(2 + legalMoves[i].fromCol*33, 2 + legalMoves[i].fromRow*33, 29, 29);
                    g.drawRect(3 + legalMoves[i].fromCol*33, 3 + legalMoves[i].fromRow*33, 27, 27);
                }

                if (selectedRow >= 0) {
                    g.setColor(Color.white);
                    g.drawRect(2 + selectedCol*33, 2 + selectedRow*33, 29, 29);
                    g.drawRect(3 + selectedCol*33, 3 + selectedRow*33, 27, 27);
                    g.setColor(Color.green);
                    for (int i = 0; i < legalMoves.length; i++) {
                        if (legalMoves[i].fromCol == selectedCol && legalMoves[i].fromRow == selectedRow) {
                            g.drawRect(2 + legalMoves[i].toCol*33, 2 + legalMoves[i].toRow*33, 29, 29);
                            g.drawRect(3 + legalMoves[i].toCol*33, 3 + legalMoves[i].toRow*33, 27, 27);
                        }
                    }
                }
            }

        }  // end paintComponent()

        public void mousePressed(MouseEvent evt) {
            if (gameInProgress == false)
                message.setText("Click \"New Game\" to start a new game.");
            else {
                int col = (evt.getX() - 2) / 33;
                int row = (evt.getY() - 2) / 33;
                if (col >= 0 && col < 8 && row >= 0 && row < 8)
                    doClickSquare(row,col);
            }
        }



    }

}

