/*************************************************************
File: chessboard.java
Date: September 10, 2005

Sean Farrell's chess engine. Goes with gui in ChessSwing.java
*************************************************************/

import java.util.*;
import java.io.*;
import java.text.*;

class moveList {
   String move;
   moveList next;

   moveList() {
      move = null;
      next = null;
   }
};



class chessboard {

   /***** constants *****/
   static final byte NaP = 0;
   static final byte WPawn = 1;
   static final byte WBishop = 2;
   static final byte WKnight = 3;
   static final byte WRook = 4;
   static final byte WQueen = 5;
   static final byte WKing = 6;
   static final byte BPawn = 7;
   static final byte BBishop = 8;
   static final byte BKnight = 9;
   static final byte BRook = 10;
   static final byte BQueen = 11;
   static final byte BKing = 12;

   /***** variables *****/
   byte[] board = new byte[64];      // board
   byte from;                        // from square #
   byte to;                          // to square #
   byte lastmove;                    // sqr # if last move was pawn out two, else -1
   byte white_king;                  // sqr white king is on
   byte black_king;                  // sqr black king is on
   boolean turn;                     // true-white, false-black(computer)
   byte[] black_cap = new byte[16];  // white has captured black pieces
   byte[] white_cap = new byte[16];  // black has captured white pieces
   boolean white_king_moved;         // castling stuff
   boolean black_king_moved;         // .
   boolean white_rook_left_moved;    // .
   boolean white_rook_right_moved;   // .
   boolean black_rook_left_moved;    // .
   boolean black_rook_right_moved;   // .
   boolean captured;                 // true-captured piece
   boolean promote;                  // true-pawn promoted
   byte result;                      // 0-white wins, 1-black wins, 2-draw
   moveList moves;
   moveList recentmove;

   /***** Initialize board *****/
   chessboard() {
      initpieces();
   }



   void initpieces() {
      int i;

      lastmove = -1;
      turn = true;
      white_king_moved = false;
      black_king_moved = false;
      white_rook_left_moved = false;
      white_rook_right_moved = false;
      black_rook_left_moved = false;
      black_rook_right_moved = false;
      captured = false;
      promote = false;
      result = -1;
      moves = null;

      for(i = 0; i < 16; ++i) {
         black_cap[i] = NaP;
         white_cap[i] = NaP;
      }

      /*** Row 1 ***/
      board[0] = BRook;
      board[1] = BKnight;
      board[2] = BBishop;
      board[3] = BQueen;
      board[4] = BKing;
      black_king = 4;
      board[5] = BBishop;
      board[6] = BKnight;
      board[7] = BRook;

      /*** Row 2 ***/
      for(i = 8; i < 16; ++i) {
         board[i] = BPawn;
      }

      /*** Rows 3-6 ***/
      for(i = 16; i < 48; ++i) {
         board[i] = NaP;
      }

      /*** Row 7 ***/
      for(i = 48; i < 56; ++i) {
         board[i] = WPawn;
      }

      /*** Row 8 ***/
      board[56] = WRook;
      board[57] = WKnight;
      board[58] = WBishop;
      board[59] = WQueen;
      board[60] = WKing;
      white_king = 60;
      board[61] = WBishop;
      board[62] = WKnight;
      board[63] = WRook;
   }



   /***** saves game by writing moves to file *****/
   int writegame(String file) {
      moveList copymoves = moves;
      int movenumber = 1;
      int charlimit = 0;     // limits output to 80 char line
      int temp = 0;
      PrintWriter p;
      String res_str, s;

      try {
         p = new PrintWriter(new BufferedWriter(new FileWriter(file)));
         p.println("[Event \"Example Game\"]");
         p.println("[Site \"PSU\"]");
         SimpleDateFormat dateformat = new SimpleDateFormat("yyyy.MM.dd");
         Date date = new Date();
         String strdate = dateformat.format(date);
         p.println("[Date \"" + strdate + "\"]");
         p.println("[Round \"1\"]");
         p.println("[White \"User\"]");
         p.println("[Black \"Computer\"]");
         p.print("[Result \"");
         if(result == 0) {
            res_str = "1-0";
         } else if(result == 1) {
            res_str = "0-1";
         } else if(result == 2) {
            res_str = "1/2-1/2";
         } else {
            res_str = "*";
         }
         p.print(res_str);
         p.println("\"]");
         p.println();
         while(copymoves != null) {
            temp = 2 + copymoves.move.length();
            s = Integer.toString(movenumber);
            temp += s.length();
            charlimit += temp;
            if(charlimit > 80) {
               charlimit = temp;
               p.println();
            }
            p.print(movenumber + ". " + copymoves.move);
            copymoves = copymoves.next;
            if(copymoves != null) {
               temp = 2 + copymoves.move.length();
               charlimit += temp;
               if(charlimit > 80) {
                  charlimit = temp;
                  p.println();
               } else {
                  p.print(" ");
               }
               ++movenumber;
               p.print(copymoves.move + " ");
               copymoves = copymoves.next;
            }
         }
         p.println();
         p.println(res_str);
         p.close();
         return 0;
      } catch (Exception e) {
         return -1;
      }
   }
 


   /***** reads game by reading moves from file *****/
   int readgame(String file) {
      BufferedReader in;
      String line = new String();
      String[] tokens = new String[40];
      int i;
      char c;
      moveList temp = null;
      turn = true;

      try {
         in = new BufferedReader(new FileReader(file));
         initpieces();
         moves = new moveList();
         recentmove = moves;
         while((line = in.readLine()) != null) {
            if(line.length() > 0) {
               if(line.charAt(0) != '[') {
                  tokens = line.split("\\s");
                  for(i = 0; i < tokens.length; ++i) {
                     c = tokens[i].charAt(0);
                     if(c != '*') {
                        if(c < '0' || c > '9') {
                           recentmove.move = new String(tokens[i]);   // log move
                           if(createmove(tokens[i]) == -1) {          // do move
                              recentmove.next = null;
                              return -1;
                           }
                           recentmove.next = new moveList();
                           temp = recentmove;
                           recentmove = recentmove.next;
                           turn = !turn;
                        } else if(tokens[i].compareTo("1-0") == 0) {
                           result = 0;
                        } else if(tokens[i].compareTo("0-1") == 0) {
                           result = 1;
                        } else if(tokens[i].compareTo("1/2-1/2") == 0) {
                           result = 2;
                        }
                     }
                  }
               }
            }
         }
         temp.next = null;
         recentmove = null;
         recentmove = temp;
         in.close();
         return 0;
      } catch (Exception e) {
         return -1;
      }
   }



   /***** does move given to it by readgame() *****/
   /***** 0-white, 1-black *****/
   int createmove(String fullmove) {
      String move;
      int row, col, colto, i;
      char c = fullmove.charAt(0);
      int length = fullmove.length();
      from = 0;
      to = 0;

      if(c == 'O') {
         if(turn == true) {
            from = 60;
         } else {
            from = 4;
         }
         if(length == 3) {
            if(turn == true) {
               to = 62;
            } else {
               to = 6;
            }
         } else {
            if(turn == true) {
               to = 58;
            } else {
               to = 2;
            }
         }
      } else if(c >= 'B' && c <= 'R') {
         byte piece = piecetoi(c);
         c = fullmove.charAt(length-1);
         if(c == '+' || c == '#') {
            move = new String(fullmove.substring(0, length-1));
         } else {
            move = new String(fullmove);
         }
         length = move.length();

         if(length == 3) {               // Ex. Qb8
            to = (byte) (rowtoi(move.charAt(2)) * 8 + coltoi(move.charAt(1)));
            return findpiece_all(piece);
         } else if(length == 4) {        // Ex.  Qxb8, Qdb8, Q3b8
            to = (byte) (rowtoi(move.charAt(3)) * 8 + coltoi(move.charAt(2)));
            c = move.charAt(1);
            if(c >= 'a' && c <= 'h') {
               return findpiece_col(piece, coltoi(c));
            } else if(c == 'x') {
               return findpiece_all(piece);
            } else {
               return findpiece_row(piece, rowtoi(c));
            }
         } else if(length == 5) {       // Ex. Qd3b8, Qdxb8, Q3xb8
            to = (byte) (rowtoi(move.charAt(4)) * 8 + coltoi(move.charAt(3)));
            c = move.charAt(1);
            if(c >= 'a' && c <= 'h') {
               c = move.charAt(2);
               if(c == 'x') {
                  return findpiece_col(piece, coltoi(move.charAt(1)));
               } else {
                  from = (byte) (rowtoi(c) * 8 + coltoi(move.charAt(1)));
                  // fall through
               }
            } else {
               return findpiece_row(piece, rowtoi(c));
            }
         } else {                      // Ex. Qd3xb8
            to = (byte) (rowtoi(move.charAt(5)) * 8 + coltoi(move.charAt(4)));
            from = (byte) (rowtoi(move.charAt(2)) * 8 + coltoi(move.charAt(1)));
            // fall through
         }
      } else {
         col = coltoi(c);
         c = fullmove.charAt(1);
         if(c == 'x') {                // Ex. dxe4
            row = rowtoi(fullmove.charAt(3)) * 8;
            colto = coltoi(fullmove.charAt(2));
            to = (byte) (row + colto);
            if(turn == true) {
               from = (byte) (row + 8 + colto + (col - colto));
            } else {
               from = (byte) (row - 8 + colto + (col - colto));
            }
         } else {                       // Ex. e4
            to = (byte) (rowtoi(c) * 8 + col);
            if(turn == true) {
               from = (byte) (to + 8);
               if(LegalMove(0) == -1) {
                  from += 8;
                  return LegalMove(0);
               } else {
                  return 0;
               }
            } else {
               from = (byte) (to - 8);
               if(LegalMove(0) == -1) {
                  from -= 8;
                  return LegalMove(0);
               } else {
                  return 0;
               }
            }
         }
      }
      return LegalMove(0);
   }



   /***** called by createmove() *****/
   /***** find piece anywhere on board *****/
   int findpiece_all(byte piece) {
      int k, i, kx8;

      for(k = 0; k < 8; ++k) {
         kx8 = k * 8;
         for(i = 0; i < 8; ++i) {
            from = (byte) (kx8 + i);
            if(board[from] == piece) {
               if(LegalMove(0) == 0) {
                  return 0;
               }
            }
         }
      }
      return -1;
   }



   /***** called by createmove() *****/
   /***** find piece on row *****/
   int findpiece_row(byte piece, int row) {
      for(int i = 0; i < 8; ++i) {
         from = (byte) (row * 8 + i);
         if(board[from] == piece) {
            if(LegalMove(0) == 0) {
               return 0;
            }
         }
      }
      return -1;
   }



   /***** called by createmove() *****/
   /***** find piece on col *****/
   int findpiece_col(byte piece, int col) {
      for(int i = 0; i < 8; ++i) {
         from = (byte) (i * 8 + col);
         if(board[from] == piece) {
            if(LegalMove(0) == 0) {
               return 0;
            }
         }
      }
      return -1;
   }



   /***** keeps track of all the moves *****/
   /***** val-2="O-O", val-3="O-O-O"   *****/
   /***** val-4="+", val-5="#"         *****/
   void recordmove(int value) {
      char[] mv = {' ', ' ', ' ', ' ', ' ', ' ', ' '};
      int index = 0;
      int num;
      byte i, copyfrom, copypiece;
      boolean bcol = false;
      boolean brow = false;
      char rowvalue = '\0';

      if(moves == null) {
         moves = new moveList();
         recentmove = moves;
      } else {
         recentmove = moves;
         while(recentmove.next != null) {
            recentmove = recentmove.next;
         }
         recentmove.next = new moveList();
         recentmove = recentmove.next;
         recentmove.next = null;
      }

      if(value == 2) {
         recentmove.move = "O-O";
      } else if(value == 3) {
         recentmove.move = "O-O-O";
      } else {
         if(promote == true) {
            if(captured == true) {
               mv[0] = itocol(col(from));
               mv[1] = 'x';
               index = 2;
            }
            mv[index] = itocol(col(to));
            mv[index+1] = itorow(row(to));
            mv[index+2] = '=';
            mv[index+3] = 'Q';
            index += 4;
         } else {
            if(board[to] == WPawn || board[to] == BPawn) {
               if(captured == true) {
                  mv[0] = itocol(col(from));
                  mv[1] = 'x';
                  index = 2;
               }
            } else {
               if(board[to] == WKnight || board[to] == BKnight) {
                  mv[0] = 'N';
               } else if(board[to] == WBishop || board[to] == BBishop) {
                  mv[0] = 'B';
               } else if(board[to] == WRook || board[to] == BRook) {
                  mv[0] = 'R';
               } else if(board[to] == WQueen || board[to] == BQueen) {
                  mv[0] = 'Q';
               } else if(board[to] == WKing || board[to] == BKing) {
                  mv[0] = 'K';
               }
               ++index;

               /***** Check for Disambiguation *****/
               copyfrom = from;
               copypiece = board[to];
               board[to] = NaP;
               turn = !turn;
               for(i = 0; i < 64; ++i) {
                  if(board[i] == copypiece) {
                     from = i;
                     if(LegalMove(1) != -1) {
                        if(col(copyfrom) != col(i)) {
                           if(bcol == false) {
                              mv[index] = itocol(col(copyfrom));
                              ++index;
                              bcol = true;
                           }
                        } else if(row(copyfrom) != row(i) && brow == false) {
                           rowvalue = itorow(row(copyfrom));
                           brow = true;
                        }
                     }
                  }
               }
               if(brow == true) {
                  mv[index] = rowvalue;
                  ++index;
               }
               turn = !turn;
               board[to] = copypiece;
               from = copyfrom;

               if(captured == true) {
                  mv[index] = 'x';
                  ++index;
               }
            }
            mv[index] = itocol(col(to));
            ++index;
            mv[index] = itorow(row(to));
            ++index;
         }
         if(value == 4) {
            mv[index] = '+';
            ++index;
         } else if(value == 5) {
            mv[index] = '#';
            ++index;
         }
         recentmove.move = new String(mv, 0, index);
      }
      return;
   }



   /***** external piece ident to internal *****/
   byte piecetoi(char c) {
      if(c == 'N') {
         if(turn == true) {
            return WKnight;
         } else {
            return BKnight;
         }
      } else if(c == 'B') {
         if(turn == true) {
            return WBishop;
         } else {
            return BBishop;
         }
      } else if(c == 'R') {
         if(turn == true) {
            return WRook;
         } else {
            return BRook;
         }
      } else if(c == 'Q') {
         if(turn == true) {
            return WQueen;
         } else {
            return BQueen;
         }
      } else if(c == 'K') {
         if(turn == true) {
            return WKing;
         } else {
            return BKing;
         }
      } else {
         if(turn == true) {
            return WPawn;
         } else {
            return BPawn;
         }
      }
   }



   /***** internal row # to external row # *****/
   char itorow(int i) {
      switch(i) {
        case 0:
           return '8';
        case 1:
           return '7';
        case 2:
           return '6';
        case 3:
           return '5';
        case 4:
           return '4';
        case 5:
           return '3';
        case 6:
           return '2';
        case 7:
           return '1';
      }
      return ' ';
   }



   /***** external row # to internal row # *****/
   int rowtoi(char c) {
      if(c == '8') {
         return 0;
      } else if(c == '7') {
         return 1;
      } else if(c == '6') {
         return 2;
      } else if(c == '5') {
         return 3;
      } else if(c == '4') {
         return 4;
      } else if(c == '3') {
         return 5;
      } else if(c == '2') {
         return 6;
      } else if(c == '1') {
         return 7;
      }
      return -1;
   }



   /***** internal col # to external col letter *****/
   char itocol(int i) {
      switch(i) {
        case 0:
           return 'a';
        case 1:
           return 'b';
        case 2:
           return 'c';
        case 3:
           return 'd';
        case 4:
           return 'e';
        case 5:
           return 'f';
        case 6:
           return 'g';
        case 7:
           return 'h';
      }
      return ' ';
   }



   /***** external col letter to internal col # *****/
   int coltoi(char c) {
      if(c == 'a') {
         return 0;
      } else if(c == 'b') {
         return 1;
      } else if(c == 'c') {
         return 2;
      } else if(c == 'd') {
         return 3;
      } else if(c == 'e') {
         return 4;
      } else if(c == 'f') {
         return 5;
      } else if(c == 'g') {
         return 6;
      } else if(c == 'h') {
         return 7;
      }
      return -1;
   }



   /***** returns the row that sqr is on *****/
   int row(int sqr) {
      int row = sqr / 8;
  
      if(row >= 0 && row <= 7) {
         return row;
      } else {
         return -1;
      }
   }



   /***** returns the col that sqr is on *****/
   int col(int sqr) {
      return (sqr % 8);
   }



   /***** if do_move=1, then just see if legal, else 0 do the move *****/
   /***** returns 0 if legal, -1 if not, 2 if castle *****/
   int LegalMove(int do_move) {
      int temp, error, i;
      int incr = 0;
      int from_row = row(from);
      int from_col = col(from);
      int to_row = row(to);
      int to_col = col(to);
      byte temp_king;

      if(from_row == -1 || to_row == -1) {
         return -1;
      }

      switch(board[from]) {
        case NaP:
           return -1;
        case BPawn:
           temp = from + 8;
           if(to_row == (from_row+1)) {
              if(to_col == (from_col+1) || to_col == (from_col-1)) {
                 if(board[to] >= WPawn && board[to] <= WKing) {
                    return movepiece(do_move);     // Capture white piece
                 } else if(lastmove != -1 && to == (lastmove+8) && board[to] == NaP) {
                    int copylastmove = lastmove;
                    if(movepiece(do_move) == 0) {
                       if(do_move == 1) {
                          return 0;
                       }
                       board[copylastmove] = NaP;      // do en passant
                       captured = true;
                       for(i = 0; i < 16; ++i) {
                          if(white_cap[i] == NaP) {
                             white_cap[i] = WPawn;
                             i = 16;
                          }
                       }
                       return 0;
                    } else {
                       return -1;
                    }
                 }
              }
              if(board[temp] != NaP) {
                 return -1;
              }
              if(temp == to) {
                 return movepiece(do_move);        // Move out one
              }
           }
           if(from_row == 1) {
              if(board[temp] != NaP) {
                 return -1;
              }
              temp += 8;
              if(board[temp] != NaP) {
                 return -1;
              }
              if(temp == to) {
                 return movepiece(do_move);        // Move out two
              }
           }
           return -1;
        case WPawn:
           temp = from - 8;
           if(to_row == (from_row-1)) {
              if(to_col == (from_col+1) || to_col == (from_col-1)) {
                 if(board[to] >= BPawn && board[to] <= BKing) {
                    return movepiece(do_move);     // Capture black piece
                 } else if(lastmove != -1 && to == (lastmove-8) && board[to] == NaP) {
                    int copylastmove = lastmove;
                    if(movepiece(do_move) == 0) {
                       if(do_move == 1) {
                          return 0;
                       }
                       board[copylastmove] = NaP;      // do en passant
                       captured = true;
                       for(i = 0; i < 16; ++i) {
                          if(black_cap[i] == NaP) {
                             black_cap[i] = BPawn;
                             i = 16;
                          }
                       }
                       return 0;
                    } else {
                       return -1;
                    }
                 }
              }
              if(board[temp] != NaP) {
                 return -1;
              }
              if(temp == to) {
                 return movepiece(do_move);        // Move out one
              }
           }
           if(from_row == 6) {
              if(board[temp] != NaP) {
                 return -1;
              }
              temp -= 8;
              if(board[temp] != NaP) {
                 return -1;
              }
              if(temp == to) {
                 return movepiece(do_move);        // Move out two
              }
           }
           return -1;
        case BKing:
           if(((from-1) == to && from_col != 0) || ((from+1) == to && from_col != 7) ||
              ((from+8) == to && from_row != 7) || ((from-8) == to && from_row != 0) ||
              ((from+7) == to && from_col != 0 && from_row != 7) ||
              ((from-7) == to && from_col != 7 && from_row != 0) ||
              ((from+9) == to && from_col != 7 && from_row != 7) ||
              ((from-9) == to && from_col != 0 && from_row != 0)) {
              return movepiece(do_move);
           } else if(black_king_moved == false) {  // castling
              if(black_rook_left_moved == false && to == 2) {
                 if(check(2) == 1 || check(3) == 1 || check(4) == 1) {
                    return -1;
                 }
                 if(board[1] == NaP && board[2] == NaP && board[3] == NaP) {
                    if(do_move == 0) {
                       lastmove = -1;
                       board[4] = NaP;
                       board[2] = BKing;
                       black_king = 2;
                       black_king_moved = true;
                       board[0] = NaP;
                       board[3] = BRook;
                       captured = false;
                       recordmove(3);
                       return 2;
                    }
                    return 0;
                 }
              } else if(black_rook_right_moved == false && to == 6) {
                 if(check(4) == 1 || check(5) == 1 || check(6) == 1) {
                    return -1;
                 }
                 if(board[5] == NaP && board[6] == NaP) {
                    if(do_move == 0) {
                       lastmove = -1;
                       board[4] = NaP;
                       board[6] = BKing;
                       black_king = 6;
                       black_king_moved = true;
                       board[7] = NaP;
                       board[5] = BRook;
                       captured = false;
                       recordmove(2);
                       return 2;
                    }
                    return 0;
                 }
              }
           }
           return -1;
        case WKing:
           if(((from-1) == to && from_col != 0) || ((from+1) == to && from_col != 7) ||
              ((from+8) == to && from_row != 7) || ((from-8) == to && from_row != 0) ||
              ((from+7) == to && from_col != 0 && from_row != 7) ||
              ((from-7) == to && from_col != 7 && from_row != 0) ||
              ((from+9) == to && from_col != 7 && from_row != 7) ||
              ((from-9) == to && from_col != 0 && from_row != 0)) {
              return movepiece(do_move);
           } else if(white_king_moved == false) {  // castling
              if(white_rook_left_moved == false && to == 58) {
                 if(check(58) == 1 || check(59) == 1 || check(60) == 1) {
                    return -1;
                 }
                 if(board[57] == NaP && board[58] == NaP && board[59] == NaP) {
                    if(do_move == 0) {
                       lastmove = -1;
                       board[60] = NaP;
                       board[58] = WKing;
                       white_king = 58;
                       white_king_moved = true;
                       board[56] = NaP;
                       board[59] = WRook;
                       captured = false;
                       recordmove(3);
                       return 2;
                    }
                    return 0;
                 }
              } else if(white_rook_right_moved == false && to == 62) {
                 if(check(60) == 1 || check(61) == 1 || check(62) == 1) {
                    return -1;
                 }
                 if(board[61] == NaP && board[62] == NaP) {
                    if(do_move == 0) {
                       lastmove = -1;
                       board[60] = NaP;
                       board[62] = WKing;
                       white_king = 62;
                       white_king_moved = true;
                       board[63] = NaP;
                       board[61] = WRook;
                       captured = false;
                       recordmove(2);
                       return 2;
                    }
                    return 0;
                 }
              }
           }
           return -1;
        case BKnight:
           // fall through
        case WKnight:
           if((from+17) == to || (from-15) == to) {
              if(from_col < 7) {          // 1 away from right side
                 return movepiece(do_move);
              }
           } else if((from+10) == to || (from-6) == to) {
              if(from_col < 6) {          // 2 away from right side
                 return movepiece(do_move);
              }
           } else if((from-17) == to || (from+15) == to) {
              if(from_col > 0) {          // 1 away from left side
                 return movepiece(do_move);
              }
           } else if((from-10) == to || (from+6) == to) {
              if(from_col > 1) {          // 2 away from left side
                 return movepiece(do_move);
              }
           }
           return -1;
        case BQueen:
           // fall through
        case WQueen:
           // fall through
        case BBishop:
           // fall through
        case WBishop:
           error = 0;
           if(from_col < to_col) {
              if(from_row < to_row) {         // right & down
                 incr = 9;
              } else if(from_row > to_row) {  // right & up
                 incr = -7;
              } else {
                 error = -1;
              }
           } else if(from_col > to_col) {
              if(from_row < to_row) {         // left & down
                 incr = 7;
              } else if(from_row > to_row) {  // left & up
                 incr = -9;
              } else {
                 error = -1;
              }
           } else {
              error = -1;
           }
           if(error != -1) {
              temp = to - from;
              if((temp % incr) != 0) {
                 error = -1;
              }
              temp = from + incr;
              while(temp != to && error != -1) {
                 if(board[temp] != NaP) {
                    error = -1;
                 } else {
                    temp += incr;
                 }
              }
              if(temp == to) {
                 return movepiece(do_move);
              } else if(board[from] == WBishop || board[from] == BBishop) {
                 return -1;
              }
           } else if(board[from] == WBishop || board[from] == BBishop) {
              return -1;
           }
        case BRook:
           // fall through
        case WRook:
           error = 0;
           if(from_row == to_row) {
              if(from_col > to_col) {          // move on row & left
                 incr = -1;
              } else if(from_col < to_col) {   // move on row & right
                 incr = 1;
              } else {
                 return -1;
              }
           } else if(from_col == to_col) {
              if(from_row < to_row) {          // move on col & down
                 incr = 8;
              } else if(from_row > to_row) {   // move on col & up
                 incr = -8;
              } else {
                 return -1;
              }
           } else {
              return -1;
           }
           temp = to - from;
           if((temp % incr) != 0) {
              return -1;
           }
           temp = from + incr;
           while(temp != to) {
              if(board[temp] != NaP) {
                 return -1;
              } else {
                 temp += incr;
              }
           }
           return movepiece(do_move);
        default:
           return -1;
      }
   }



   /***** if do_move=1, then just see if legal, else do the move *****/
   int movepiece(int do_move) {
      int i;
      int rc = 0;
      byte oldpiece;

      if(turn == true) {
         if((board[to] >= WPawn && board[to] <= WKing) ||
            board[from] == NaP || board[from] >= BPawn) {
            return -1;
         }
      } else {
         if((board[to] >= BPawn && board[to] <= BKing) ||
            board[from] < BPawn) {
            return -1;
         }
      }
      oldpiece = board[to];
      board[to] = board[from];
      board[from] = NaP;
      if(turn == true) {
         if(board[to] == WKing) {
            rc = check(to);
         } else {
            rc = check(white_king);
         }
      } else {
         if(board[to] == BKing) {
            rc = check(to);
         } else {
            rc = check(black_king);
         }
      }
      if(rc == 1 || do_move == 1) {        // undo the move
         board[from] = board[to];
         board[to] = oldpiece;
         if(rc == 1) {
            return -1;
         } else {
            return 0;
         }
      } else {                             // finish doing the move
         if(oldpiece != NaP) {
            captured = true;
            if(turn == true) {
               for(i = 0; i < 16; ++i) {
                  if(black_cap[i] == NaP) {
                     black_cap[i] = oldpiece;
                     i = 16;
                  }
               }
            } else {
               for(i = 0; i < 16; ++i) {
                  if(white_cap[i] == NaP) {
                     white_cap[i] = oldpiece;
                     i = 16;
                  }
               }
            }
         } else {
            captured = false;
         }
         promote = false;
         switch(board[to]) {
           case WRook:
              if(from == 56) {
                 white_rook_left_moved = true;
              } else if(from == 63) {
                 white_rook_right_moved = true;
              }
              break;
           case BRook:
              if(from == 0) {
                 black_rook_left_moved = true;
              } else if(from == 7) {
                 black_rook_right_moved = true;
              }
              break;
           case WKing:
              white_king = to;
              white_king_moved = true;
              break;
           case BKing:
              black_king = to;
              black_king_moved = true;
              break;
           case WPawn:
              if(to >= 0 && to <= 7) {      // pawn promote
                 board[to] = WQueen;
                 promote = true;
              }
              break;
           case BPawn:
              if(to >= 56 && to <= 63) {      // pawn promote
                 board[to] = BQueen;
                 promote = true;
              }
              break;
         }
         if((board[to] == WPawn && to == (from-16)) ||
            (board[to] == BPawn && to == (from+16))) {
             lastmove = to;
         } else {
             lastmove = -1;
         }
         return 0;
      }
   }



   /***** returns 1 if check, 0 if no check. king to check *****/
   int check(int king) {
      int i, col, col_k, row, row_k, temp, incr;
      byte CKnight, CQueen, CBishop, CRook, CKing;

      if(turn == true) {
         CKnight = BKnight;
         CQueen = BQueen;
         CBishop = BBishop;
         CRook = BRook;
         CKing = BKing;
      } else {
         CKnight = WKnight;
         CQueen = WQueen;
         CBishop = WBishop;
         CRook = WRook;
         CKing = WKing;
      }
      for(i = 0; i < 64; ++i) {
         col = col(i);
         row = row(i);
         col_k = col(king);
         row_k = row(king);
         if(turn == false && board[i] == WPawn && row_k == (row-1)) {
            if(king == (i-7) || king == (i-9)) {
               return 1;
            }
         }
         if(turn == true && board[i] == BPawn && row_k == (row+1)) {
            if(king == (i+7) || king == (i+9)) {
               return 1;
            }
         }
         if(board[i] == CKnight) {
            if((i+17) == king || (i-15) == king) {
               if(col < 7) {          // 1 away from right side
                  return 1;
               }
            } else if((i+10) == king || (i-6) == king) {
               if(col < 6) {          // 2 away from right side
                  return 1;
               }
            } else if((i-17) == king || (i+15) == king) {
               if(col > 0) {          // 1 away from left side
                  return 1;
               }
            } else if((i-10) == king || (i+6) == king) {
               if(col > 1) {          // 2 away from left side
                  return 1;
               }
            }
         }
         if(board[i] == CQueen || board[i] == CBishop) {
            incr = 0;
            if(col < col_k) {
               if(row < row_k) {         // right & down
                  incr = 9;
               } else if(row > row_k) {  // right & up
                  incr = -7;
               }
            } else if(col > col_k) {
               if(row < row_k) {         // left & down
                  incr = 7;
               } else if(row > row_k) {  // left & up
                  incr = -9;
               }
            }
            if(incr != 0) {
               temp = king - i;
               if((temp % incr) == 0) {
                  temp = i + incr;
                  while(temp != king) {
                     if(board[temp] != NaP) {
                        break;
                     } else {
                        temp += incr;
                     }
                  }
                  if(temp == king) {
                     return 1;
                  }
               }
            }
         }
         if(board[i] == CQueen || board[i] == CRook) {
            incr = 0;
            if(row == row_k) {
               if(col > col_k) {          // move on row & left
                  incr = -1;
               } else if(col < col_k) {   // move on row & right
                  incr = 1;
               }
            } else if(col == col_k) {
               if(row < row_k) {          // move on col & down
                  incr = 8;
               } else if(row > row_k) {   // move on col & up
                  incr = -8;
               }
            }
            if(incr != 0) {
               temp = king - i;
               if((temp % incr) == 0) {
                  temp = i + incr;
                  while(temp != king) {
                     if(board[temp] != NaP) {
                        break;
                     } else {
                        temp += incr;
                     }
                  }
                  if(temp == king) {
                     return 1;
                  }
               }
            }
         }
         if(board[i] == CKing) {
            if(col > 0) {
               if(king == (i-1)) {
                  return 1;
               } else if(row > 0 && king == (i-9)) {
                  return 1;
               } else if(row < 7 && king == (i+7)) {
                  return 1;
               }
            }
            if(col < 7) {
               if(king == (i+1)) {
                  return 1;
               } else if(row > 0 && king == (i-7)) {
                  return 1;
               } else if(row < 7 && king == (i+9)) {
                  return 1;
               }
            }
            if(row > 0) {
               if(king == (i-8)) {
                  return 1;
               }
            }
            if(row < 7) {
               if(king == (i+8)) {
                  return 1;
               }
            }
         }
      }
      return 0;
   }



   /***** if checkmate return 1, else return 0 *****/
   int checkmate() {
      byte i, k;
      byte lower, upper;
      boolean check_mate = true;
      byte from_copy = from;
      byte to_copy = to;

      if(turn == true) {
         lower = WPawn;
         upper = WKing;
      } else {
         lower = BPawn;
         upper = BKing;
      }
      for(i = 0; i < 64; ++i) {
         if(board[i] >= lower && board[i] <= upper) {
            for(k = 0; k < 64; ++k) {
               from = i;
               to = k;
               if(LegalMove(1) == 0) {
                  check_mate = false;
                  k = 64;
                  i = 64;
               }
            }
         }
      }
      from = from_copy;
      to = to_copy;
      if(check_mate == true) {
         return 1;
      } else {
         return 0;
      }
   }



   /***** make move for computer *****/
   int comp_move() {
      int rc = 0;
      if(best_capture() == 0) {
         rc = LegalMove(0);
      } else {
         rc = rand_move();
      }
      turn = true;
      if(rc == 2) {
         return 0;
      }
      if(check(white_king) == 1) {
         if(checkmate() == 1) {
            recordmove(5);
            result = 1;
            return 2;
         } else {
            recordmove(4);
            return 1;
         }
      } else {
         recordmove(0);
         if(checkmate() == 1) {
            result = 2;
            return 3;
         } else {
            return 0;
         }
      }
   }



   /***** used by comp_move *****/
   /***** make random move for computer *****/
   int rand_move() {
      Random rand = new Random();
      int rc = 0;

      do {
        do {
           from = (byte) rand.nextInt(64);
        } while(board[from] < BPawn);
        to = (byte) rand.nextInt(64);
        rc = LegalMove(0);
      } while(rc == -1);
      return rc;
   }



   /***** used by comp_move *****/
   /***** "best capture" for computer *****/
   int best_capture() {
      byte i, k;
      byte best_from = -1;
      byte best_to = -1;

      for(i = 0; i < 64; ++i) {
         if(board[i] >= BPawn && board[i] <= BKing) {
            for(k = 0; k < 64; ++k) {
               if(board[k] >= WPawn && board[k] <= WKing) {
                  if(best_to == -1 || board[k] > board[best_to]) {
                     from = i;
                     to = k;
                     if(LegalMove(1) == 0) {
                        best_from = i;
                        best_to = k;
                     }
                  }
               }
            }
         }
      }
      if(best_from == -1) {
         return -1;
      } else {
         from = best_from;
         to = best_to;
         return 0;
      }
   }



   /***** player's move *****/
   int move() {
      int rc = LegalMove(0);
      if(rc == 2) {
         turn = false;
         return 0;
      }
      if(rc == 0) {
         turn = false;
         if(check(black_king) == 1) {
            if(checkmate() == 1) {
               recordmove(5);
               result = 0;
               return 2;
            } else {
               recordmove(4);
               return 1;
            }
         } else {
            recordmove(0);
            if(checkmate() == 1) {
               result = 2;
               return 3;
            } else {
               return 0;
            }
         }
      } else {
         return -1;
      }
   }

}