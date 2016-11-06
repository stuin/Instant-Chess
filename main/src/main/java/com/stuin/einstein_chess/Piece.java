package com.stuin.einstein_chess;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class Piece {
    int x;
    int y;
    private int i;
    int type = 6;
    boolean black = false;

    private boolean moved = false;
    private TextView[][] board;
    Game game;

    void showPosition(boolean end) {
            board = game.board;
            TextView t = board[x][y];
            switch(type) {
                case 1:
                    t.setText("K");
                    break;
                case 2:
                    t.setText("Q");
                    break;
                case 3:
                    t.setText("R");
                    break;
                case 4:
                    t.setText("B");
                    break;
                case 5:
                    t.setText("N");
                    break;
                case 6:
                    t.setText("P");
                    break;
            }

            if(type != 0) {
                if (black) {
                    t.setBackgroundColor(Color.DKGRAY);
                    t.setTextColor(Color.BLACK);
                } else {
                    t.setBackgroundColor(Color.LTGRAY);
                    t.setTextColor(Color.WHITE);
                }

                i = t.getId();
                if (!end) t.setOnClickListener(selectListener);
            }
    }

    private boolean contains(int x, int y) {
        return (x < 8 && x >= 0 && y < 8 && y >= 0);
    }

    private TextView.OnClickListener selectListener = new TextView.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(black == game.blackTurn) {
                List<Point> moves = new ArrayList<>();
                boolean moving = true;
                int dir = 1;
                while(dir != 0) {
                    int nX = x;
                    int nY = y;
                    switch (type) {
                        case 1:
                            //King
                            moves.add(new Point(nX + dir, nY + dir));
                            moves.add(new Point(nX - dir, nY + dir));
                            moves.add(new Point(nX + dir, nY));
                            moves.add(new Point(nX, nY + dir));
                            break;
                        case 2:
                        case 3:
                            //Queen & Rook
                            while (contains(nX, nY) && moving) {
                                nX += dir;
                                moves.add(new Point(nX, nY));
                                if (contains(nX, nY) && board[nX][nY].getText().length() != 0) moving = false;
                            }
                            nX = x;
                            moving = true;
                            while (contains(nX, nY) && moving) {
                                nY += dir;
                                moves.add(new Point(nX, nY));
                                if (contains(nX, nY) && board[nX][nY].length() != 0) moving = false;
                            }
                            nY = y;
                            moving = true;
                            if (type == 3) break;
                        case 4:
                            //Bishop
                            while (contains(nX, nY) && moving) {
                                nX += dir;
                                nY += dir;
                                moves.add(new Point(nX, nY));
                                if (contains(nX, nY) && board[nX][nY].length() != 0) moving = false;
                            }
                            nX = x;
                            nY = y;
                            moving = true;
                            while (contains(nX, nY) && moving) {
                                nX -= dir;
                                nY += dir;
                                moves.add(new Point(nX, nY));
                                if (contains(nX, nY) && board[nX][nY].length() != 0) moving = false;
                            }
                            moving = true;
                            break;
                        case 5:
                            //Knight
                            moves.add(new Point(x + 2 * dir, y + dir));
                            moves.add(new Point(x + 2 * dir, y - dir));
                            moves.add(new Point(x + dir, y + 2 * dir));
                            moves.add(new Point(x - dir, y + 2 * dir));
                            break;
                        case 6:
                            //Pawn
                            if (black) nY++;
                            else nY--;
                            if (board[nX][nY].length() == 0 && dir == 1) {
                                moves.add(new Point(nX, nY));
                                if (board[nX][nY + 1].length() == 0 && !moved && black)
                                    moves.add(new Point(nX, nY + 1));
                                if (board[nX][nY - 1].length() == 0 && !moved && !black)
                                    moves.add(new Point(nX, nY - 1));
                            }
                            if (contains(nX + dir, nY) && board[nX + dir][nY].length() != 0)
                                moves.add(new Point(nX + dir, nY));
                            break;
                    }
                    if (dir == 1) dir = -1;
                    else dir = 0;
                }

                List<Point> remove = new ArrayList<>();
                for(Point p : moves)
                    if(contains(p.x, p.y)) {
                        if(black == (board[p.x][p.y].getCurrentTextColor() == Color.BLACK) && board[p.x][p.y].length() != 0)
                            remove.add(p);
                        if(p.x == x && p.y == y) remove.add(p);
                    } else remove.add(p);
                moves.removeAll(remove);

                game.setBoard();
                for(Point p : moves) {
                    if(board[p.x][p.y].length() == 0) board[p.x][p.y].setBackgroundColor(Color.GREEN);
                    else board[p.x][p.y].setBackgroundColor(Color.RED);
                    board[p.x][p.y].setOnClickListener(moveListener);
                }

                if(type == 1 && !moved) {
                    if(board[1][y].length() == 0 && board[2][y].length() == 0 && (!black || (black && board[3][y].length() == 0))) {
                        board[1][y].setBackgroundColor(Color.GREEN);
                        board[1][y].setOnClickListener(castleListener);
                    }
                    if(board[6][y].length() == 0 && board[5][y].length() == 0 && (black || (!black && board[4][y].length() == 0))) {
                        board[6][y].setBackgroundColor(Color.GREEN);
                        board[6][y].setOnClickListener(castleListener);
                    }
                }
            }
        }
    };

    private TextView.OnClickListener moveListener = new TextView.OnClickListener() {
        @Override
        public void onClick(View view) {
            i = view.getId();
            x = i % 8;
            y = i / 8;
            moved = true;

            if(game.einstein && type > 1) {
                if(type > 2) type--;
                else type = 6;
            }

            if(board[x][y].length() != 0) {
                for(int j = 0; j < game.set.length; j++) if(game.set[j].i == i && game.set[j].black != black) game.set[j].type = 0;
                game.win = board[x][y].getText().equals("K");
            }

            game.blackTurn = !game.blackTurn;
            game.setBoard();
        }
    };

    private TextView.OnClickListener castleListener = new TextView.OnClickListener() {
        @Override
        public void onClick(View view) {
            i = view.getId();
            int nX = i % 8;

            if(nX < x) for(Piece p : game.set) if(p.i == i - 1)  {
                p.x = 2;
                moved = true;
                if(game.einstein) p.type = 4;
            }

            if(nX > x) for(Piece p : game.set) if(p.i == i + 1)  {
                p.x = 5;
                moved = true;
                if(game.einstein) p.type = 4;
            }

            moveListener.onClick(view);
        }
    };
}
