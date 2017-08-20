/*************************************************************
File: ChessSwing.java
Date: September 10, 2005

Sean Farrell's chess gui. Goes with engine in chessboard.java
*************************************************************/

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class ChessSwing extends JComponent implements ActionListener {
   JTextArea textArea;
   JLabel statusLabel;
   JFileChooser fc;
   JMenuItem menuItemNew, menuItemOpen, menuItemSave;
   Color DarkSquare;
   Font monofont;
   chessboard game;
   boolean gameover, click;
   Image BRook, BBishop, BKnight, BQueen, BKing, BPawn, 
         WRook, WBishop, WKnight, WQueen, WKing, WPawn;
   int movenumber;
   CheckerBoard CB;

   public ChessSwing() {
      DarkSquare = new Color(0x009000);
      game = new chessboard();
      gameover = false;
      click = false;
      movenumber = 1;
      monofont = new Font("Monospaced", Font.PLAIN, 12);

      // Get images
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      BRook = toolkit.getImage("image/BRook.gif");
      BBishop = toolkit.getImage("image/BBishop.gif");
      BKnight = toolkit.getImage("image/BKnight.gif");
      BQueen = toolkit.getImage("image/BQueen.gif");
      BKing = toolkit.getImage("image/BKing.gif");
      BPawn = toolkit.getImage("image/BPawn.gif");
      WRook = toolkit.getImage("image/WRook.gif");
      WBishop = toolkit.getImage("image/WBishop.gif");
      WKnight = toolkit.getImage("image/WKnight.gif");
      WQueen = toolkit.getImage("image/WQueen.gif");
      WKing = toolkit.getImage("image/WKing.gif");
      WPawn = toolkit.getImage("image/WPawn.gif");
   }


   private class RowBar extends JPanel {
      public Dimension getPreferredSize() {
         return new Dimension(25, 400);
      }

      public boolean isOpaque() {
         return false;
      }

      protected void paintComponent(Graphics g) {
         Dimension size = getSize();
         int x = 5;
         int y = (size.height) / 8;
         g.setFont(new Font("Serif",Font.BOLD,24));
         g.setColor(DarkSquare);
         g.drawString("8", x, y);
         g.drawString("7", x, (y*2));
         g.drawString("6", x, (y*3));
         g.drawString("5", x, (y*4));
         g.drawString("4", x, (y*5));
         g.drawString("3", x, (y*6));
         g.drawString("2", x, (y*7));
         g.drawString("1", x, (y*8));
      }
   }


   private class ColBar extends JPanel {
      public Dimension getPreferredSize() {
         return new Dimension(475, 25);
      }

      public boolean isOpaque() {
         return false;
      }

      protected void paintComponent(Graphics g) {
         Dimension size = getSize();
         int x = ((size.width)-75) / 8;
         int y = (size.height) - 5;
         g.setFont(new Font("Serif",Font.BOLD,24));
         g.setColor(DarkSquare);
         g.drawString("a", 25, y);
         g.drawString("b", 25+x, y);
         g.drawString("c", 25+(x*2), y);
         g.drawString("d", 25+(x*3), y);
         g.drawString("e", 25+(x*4), y);
         g.drawString("f", 25+(x*5), y);
         g.drawString("g", 25+(x*6), y);
         g.drawString("h", 25+(x*7), y);
      }
   }


   private class CheckerBoard extends JPanel
                              implements MouseListener {
      int w, h;

      public CheckerBoard() {
         addMouseListener(this);
      }

      public Dimension getPreferredSize() {
         return new Dimension(450, 400);
      }

      public boolean isOpaque() {
         return false;
      }

      protected void paintComponent(Graphics g) {
         Dimension size = getSize();
         w = (size.width - 50) / 8;
         h = size.height / 8;
         int capw = (50 - 3) / 2;
         int caph = size.height / 2;
         int x = size.width - 50;
         int i, j, row, col;

         // draw checkerboard - rows 8,6,4,2
         for(j = 0; j <= (h*6); j+=(h*2)) {
            g.setColor(Color.white);
            for(i = 0; i <= 6; i+=2) {
               g.fillRect((w*i), j, w, h);
            }
            g.setColor(DarkSquare);
            for(i = 1; i <= 7; i+=2) {
               g.fillRect((w*i), j, w, h);
            }
         }
         // rows 7,5,3,1
         for(j = h; j <= (h*7); j+=(h*2)) {
            g.setColor(DarkSquare);
            for(i = 0; i <= 6; i+=2) {
               g.fillRect((w*i), j, w, h);
            }
            g.setColor(Color.white);
            for(i = 1; i <= 7; i+=2) {
               g.fillRect((w*i), j, w, h);
            }
         }

         // place pieces on board
         for(i = 0; i < 64; ++i) {
            row = game.row(i);
            col = game.col(i);
            switch(game.board[i]) {
               case chessboard.BRook:
                  g.drawImage(BRook, (w*col), (h*row), w, h, this);
                  break;
               case chessboard.BBishop:
                  g.drawImage(BBishop, (w*col), (h*row), w, h, this);
                  break;
               case chessboard.BKnight:
                  g.drawImage(BKnight, (w*col), (h*row), w, h, this);
                  break;
               case chessboard.BQueen:
                  g.drawImage(BQueen, (w*col), (h*row), w, h, this);
                  break;
               case chessboard.BKing:
                  g.drawImage(BKing, (w*col), (h*row), w, h, this);
                  break;
               case chessboard.BPawn:
                  g.drawImage(BPawn, (w*col), (h*row), w, h, this);
                  break;
               case chessboard.WRook:
                  g.drawImage(WRook, (w*col), (h*row), w, h, this);
                  break;
               case chessboard.WBishop:
                  g.drawImage(WBishop, (w*col), (h*row), w, h, this);
                  break;
               case chessboard.WKnight:
                  g.drawImage(WKnight, (w*col), (h*row), w, h, this);
                  break;
               case chessboard.WQueen:
                  g.drawImage(WQueen, (w*col), (h*row), w, h, this);
                  break;
               case chessboard.WKing:
                  g.drawImage(WKing, (w*col), (h*row), w, h, this);
                  break;
               case chessboard.WPawn:
                  g.drawImage(WPawn, (w*col), (h*row), w, h, this);
                  break;
            }
         }

         // Draw captured pieces
         j = 0;
         for(i = 0; i < 16; ++i) {
            if(i == 8) {
               x += capw + 3;
               j = 0;
            }
            switch(game.black_cap[i]) {
               case chessboard.BRook:
                  g.drawImage(BRook, x, (capw*j+3+caph), capw, capw, this);
                  break;
               case chessboard.BBishop:
                  g.drawImage(BBishop, x, (capw*j+3+caph), capw, capw, this);
                  break;
               case chessboard.BKnight:
                  g.drawImage(BKnight, x, (capw*j+3+caph), capw, capw, this);
                  break;
               case chessboard.BQueen:
                  g.drawImage(BQueen, x, (capw*j+3+caph), capw, capw, this);
                  break;
               case chessboard.BKing:
                  g.drawImage(BKing, x, (capw*j+3+caph), capw, capw, this);
                  break;
               case chessboard.BPawn:
                  g.drawImage(BPawn, x, (capw*j+3+caph), capw, capw, this);
                  break;
            }
            switch(game.white_cap[i]) {
               case chessboard.WRook:
                  g.drawImage(WRook, x, (capw*j+3), capw, capw, this);
                  break;
               case chessboard.WBishop:
                  g.drawImage(WBishop, x, (capw*j+3), capw, capw, this);
                  break;
               case chessboard.WKnight:
                  g.drawImage(WKnight, x, (capw*j+3), capw, capw, this);
                  break;
               case chessboard.WQueen:
                  g.drawImage(WQueen, x, (capw*j+3), capw, capw, this);
                  break;
               case chessboard.WKing:
                  g.drawImage(WKing, x, (capw*j+3), capw, capw, this);
                  break;
               case chessboard.WPawn:
                  g.drawImage(WPawn, x, (capw*j+3), capw, capw, this);
                  break;
            }
            ++j;
         }
      }

      public void displaymove() {
         int spaces = 7 - game.recentmove.move.length();
         if(game.turn == false) {     // black's move now
            if(movenumber < 10) {
               textArea.append(movenumber + ".  " + game.recentmove.move);
            } else {
               textArea.append(movenumber + ". " + game.recentmove.move);
            }
            for(; spaces > 0; --spaces) {
               textArea.append(" ");
            }
         } else {
            textArea.append(" " + game.recentmove.move);
            for(; spaces > 0; --spaces) {
               textArea.append(" ");
            }
            textArea.append("\n");
            ++movenumber;
         }
      }

      public void mousePressed(MouseEvent evt) {
         int x = evt.getX();    // top left corner is (0,0)
         int y = evt.getY();
         int col = (int) (x / w);
         int row = (int) (y / h);
         int rc = 0;
         Graphics g = this.getGraphics();

         if(game.turn == true && gameover == false) {
            if(col >= 0 && col <= 7 && row >= 0 && row <= 7) {
               if(click == false) {
                  g.setColor(Color.red);
                  g.drawRect((w*col), (h*row), (w-1), (h-1));
                  game.from = (byte) (row * 8 + col);
                  click = true;
               } else {
                  game.to = (byte) (row * 8 + col);
                  click = false;
                  if(game.from == game.to) {    // deselect
                     repaint();
                  } else {
                     rc = game.move();
                     repaint();
                     if(rc == -1) {
                        statusLabel.setText("-- Invalid Move --");
                     } else {
                        displaymove();
                        if(rc == 0) {
                           statusLabel.setText("Black's Move");
                        } else if(rc == 1) {
                           statusLabel.setText("Black's Move - Check!");
                        } else if(rc == 2) {
                           statusLabel.setText("White Wins - Checkmate!");
                           gameover = true;
                           return;
                        } else if(rc == 3) {
                           statusLabel.setText("Stalemate!");
                           gameover = true;
                           return;
                        }
                        rc = game.comp_move();
                        displaymove();
                        repaint();
                        if(rc == 0) {
                           statusLabel.setText("White's Move");
                        } else if(rc == 1) {
                           statusLabel.setText("White's Move - Check!");
                        } else if(rc == 2) {
                           statusLabel.setText("Black Wins - Checkmate!");
                           gameover = true;
                           return;
                        } else if(rc == 3) {
                           statusLabel.setText("Stalemate!");
                           gameover = true;
                           return;
                        }
                     }
                  }
               }
            }
         }
      }

      // The following empty routines are required by the
      // MouseListener interface:
      public void mouseEntered(MouseEvent evt) { }
      public void mouseExited(MouseEvent evt) { }
      public void mouseClicked(MouseEvent evt) { }
      public void mouseReleased(MouseEvent evt) { }
   }


   // called by actionPerformed
   public void displayallmoves() {
      moveList displaymoves = game.moves;
      int spaces;
      int number = 1;

      textArea.setText("#  White   Black  \n");
      while(displaymoves != null) {
         spaces = 7 - displaymoves.move.length();
         if(number < 10) {
            textArea.append(number + ".  " + displaymoves.move);
         } else {
            textArea.append(number + ". " + displaymoves.move);
         }
         for(; spaces > 0; --spaces) {
            textArea.append(" ");
         }
         game.turn = false;
         displaymoves = displaymoves.next;
         if(displaymoves != null) {
            spaces = 7 - displaymoves.move.length();
            textArea.append(" " + displaymoves.move);
            for(; spaces > 0; --spaces) {
               textArea.append(" ");
            }
            textArea.append("\n");
            ++number;
            game.turn = true;
            displaymoves = displaymoves.next;
         }
      }
      movenumber = number;
   }


   public void actionPerformed(ActionEvent evt) {
      int rc;
      File file;

      if(evt.getSource() == menuItemNew) {
         game.initpieces();
         click = false;
         gameover = false;
         movenumber = 1;
         textArea.setText("#  White   Black  \n");
      } else if(evt.getSource() == menuItemOpen) {
         rc = fc.showOpenDialog(ChessSwing.this);
         if(rc == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            if(game.readgame(file.getName()) == 0) {
               displayallmoves();
               click = false;
               if(game.result == -1) {
                  if(game.turn == true) {
                     statusLabel.setText("White's Move");
                  } else {
                     statusLabel.setText("Black's Move");
                  }
                  gameover = false;
               } else {
                  if(game.result == 0) {
                     statusLabel.setText("White Wins - Checkmate!");
                  } else if(game.result == 1) {
                     statusLabel.setText("Black Wins - Checkmate!");
                  } else {
                     statusLabel.setText("Stalemate!");
                  }
                  gameover = true;
               }
            } else {
               statusLabel.setText("Invalid file.");
            }
         }
      } else if(evt.getSource() == menuItemSave) {
         rc = fc.showSaveDialog(ChessSwing.this);
         if(rc == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            game.writegame(file.getName());
         }
      }
      CB.repaint();
   }


   public void createCenterPanel(JPanel centerPanel) {
      centerPanel.add(new RowBar(), BorderLayout.WEST);
      centerPanel.add(new ColBar(), BorderLayout.SOUTH);
      CB = new CheckerBoard();
      centerPanel.add(CB, BorderLayout.CENTER);
   }


   private void createContentPane(Container pane) {
      Border blackline = BorderFactory.createLineBorder(Color.black);

      // Create centerPanel in CENTER
      JPanel centerPanel = new JPanel(new BorderLayout());
      centerPanel.setBorder(blackline);
      createCenterPanel(centerPanel);
      pane.add(centerPanel, BorderLayout.CENTER);

      // Create statusLabel in SOUTH
      statusLabel = new JLabel("New Game Started");
      statusLabel.setBorder(blackline);
      pane.add(statusLabel, BorderLayout.SOUTH);

      // Create log in EAST
      textArea = new JTextArea("#  White   Black  \n");
      textArea.setEditable(false);
      textArea.setLineWrap(true);
      textArea.setWrapStyleWord(true);
      textArea.setFont(monofont);
      JScrollPane areaScrollPane = new JScrollPane(textArea);
      areaScrollPane.setVerticalScrollBarPolicy(
                     JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      areaScrollPane.setPreferredSize(new Dimension(200, 425));
      areaScrollPane.setBorder(
         BorderFactory.createCompoundBorder(
            BorderFactory.createCompoundBorder(
                          BorderFactory.createTitledBorder("Game Log"),
                          BorderFactory.createEmptyBorder(5,5,5,5)),
            areaScrollPane.getBorder()));
      pane.add(areaScrollPane, BorderLayout.EAST);
   }


   public JMenuBar createMenuBar() {
      JMenuBar menuBar;
      JMenu menu;

      // Create the menu bar.
      menuBar = new JMenuBar();

      // Build the file menu.
      menu = new JMenu("File");
      menuBar.add(menu);

      // A group of JMenuItems
      menuItemNew = new JMenuItem("New Game");
      menuItemNew.addActionListener(this);
      menu.add(menuItemNew);

      menuItemOpen = new JMenuItem("Open Game");
      menuItemOpen.addActionListener(this);
      menu.add(menuItemOpen);

      menuItemSave = new JMenuItem("Save Game");
      menuItemSave.addActionListener(this);
      menu.add(menuItemSave);

      //Create a file chooser
      fc = new JFileChooser();

      return menuBar;
   }


   /**
    * Create the GUI and show it.  For thread safety,
    * this method should be invoked from the
    * event-dispatching thread.
    */
   private static void createAndShowGUI() {
      // Make sure we have nice window decorations.
      JFrame.setDefaultLookAndFeelDecorated(true);

      // Create and set up the window.
      JFrame frame = new JFrame("SwingChess");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      // Create and set up the content pane.
      ChessSwing app = new ChessSwing();
      frame.setJMenuBar(app.createMenuBar());
      app.createContentPane(frame.getContentPane());

      // Display the window.
      frame.pack();
      frame.setVisible(true);
   }


   public static void main(String[] args) {
      // Schedule a job for the event-dispatching thread:
      // creating and showing this application's GUI.
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            createAndShowGUI();
         }
      });
   }
}