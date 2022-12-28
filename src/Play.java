import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Play {
	static class MyFrame extends JFrame {
		
		static class XY {
			int x;
			int y;
			
			public XY(int x, int y) {
				this.x = x;
				this.y = y;
			}
		}
		
		static JPanel panelNorth;
		static JPanel panelCenter;
		static JLabel labelTitle;
		static JLabel labelMsg;
		static JPanel[][] panels = new JPanel[20][20];
		static int[][] map = new int[20][20];		// 먹이 위치 : 7
		static LinkedList<XY> snake = new LinkedList<XY>();
		static int dir = 3;			// 뱀의 이동 방향 (상 : 0, 하 : 1, 좌 : 2, 우 : 3)
		static int score = 0;		// 점수
		static int time = 0;		// 시간 (단위가 1초)
		static int timeCnt = 0;	// (단위가 200ms)
		static Timer timer = null;
		
		public MyFrame(String title) {
			super(title);
			this.setSize(400, 400);	// 창 크기
			this.setVisible(true);		// 창이 보이게 함
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		// 창이 닫히게 함
		
			initUI();
			makeSnakeList();
			startTimer();
			setKeyListener();
			eatFood();
		}
		
		public void eatFood() {
			Random rand = new Random();
			
			int randX = rand.nextInt(19);
			int randY = rand.nextInt(19);
			map[randX][randY] = 7;
		}
		
		public void setKeyListener() {
			this.addKeyListener(new KeyAdapter() {

				@Override
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_UP) {
						if(dir != 1) {
							dir = 0;
						}
					}
					else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
						if(dir != 0) {
							dir = 1;
						}
					}
					else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
						if(dir != 3) {
							dir = 2;
						}
					}
					else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
						if(dir != 2) {
							dir = 3;
						}
					}
				}
			});
		}
		
		public void startTimer() {
			timer = new Timer(250, new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					timeCnt += 1;
					
					if(timeCnt % 4 == 0) {
						time++;
						
					}
					
					moveSnake();
					updateUI();
				}
			});
			
			timer.start();	// 타이머 시작
		}
		
		public void moveSnake() {
			XY headXY = snake.get(0);
			int headX = headXY.x;
			int headY = headXY.y;
			
			// 상
			if(dir == 0) {
				boolean isColl = checkCollision(headX, headY - 1);
				if(isColl) {
					labelMsg.setText("Game Over");
					labelMsg.setForeground(Color.CYAN);
					timer.stop();
					return;
				}
				
				snake.add(0, new XY(headX, headY - 1));
				snake.remove(snake.size() - 1);
			}
			// 하
			else if(dir == 1) {
				boolean isColl = checkCollision(headX, headY + 1);
				if(isColl) {
					labelMsg.setText("Game Over");
					labelMsg.setForeground(Color.CYAN);
					timer.stop();
					return;
				}
				
				snake.add(0, new XY(headX, headY + 1));
				snake.remove(snake.size() - 1);
			}
			// 좌
			else if(dir == 2) {
				boolean isColl = checkCollision(headX - 1, headY);
				if(isColl) {
					labelMsg.setText("Game Over");
					labelMsg.setForeground(Color.CYAN);
					timer.stop();
					return;
				}
				
				snake.add(0, new XY(headX - 1, headY));
				snake.remove(snake.size() - 1);
			}
			// 우
			else {
				boolean isColl = checkCollision(headX + 1, headY);
				if(isColl) {
					labelMsg.setText("Game Over");
					labelMsg.setForeground(Color.CYAN);
					timer.stop();
					return;
				}
				
				snake.add(0, new XY(headX + 1, headY));
				snake.remove(snake.size() - 1);
			}
		}
		
		// 충돌 체크
		public boolean checkCollision(int headX, int headY) {
			// 벽을 넘어가면
			if( headX < 0 || headX > 19 || headY < 0 || headY > 19 ) {
				return true;
			}
			
			// 자기 몸에 부딪치면
			for( XY xy : snake ) {
				if(headX == xy.x && headY == xy.y) {
					return true;
				}
			}
			
			// 먹이를 먹는다면
			if(map[headY][headX] == 7) {
				map[headY][headX] = 0;
				addTail();		// 꼬리 추가
				eatFood();
				score += 1;
			}
			
			return false;
		}	// 충돌체크 끝
		
		public void addTail() {
			int tailX = snake.get(snake.size() - 1).x;
			int tailY = snake.get(snake.size() - 1).y;
			
			int tailX2 = snake.get(snake.size() - 2).x;
			int tailY2 = snake.get(snake.size() - 2).y;
			
			// 왼쪽에 붙는다
			if(tailX < tailX2) {
				snake.add(new XY(tailX - 1, tailY));
			}
			// 오른쪽에 붙는다
			else if(tailX > tailX2) {
				snake.add(new XY(tailX + 1, tailY));
			}
			// 아래쪽에 붙는다
			else if(tailY < tailY2) {
				snake.add(new XY(tailX, tailY - 1));
			}
			// 위쪽에 붙는다
			else if(tailY > tailY2) {
				snake.add(new XY(tailX, tailY + 1));
			}
		}
		
		public void updateUI() {
			labelTitle.setText("Score : " + score + ", Time : " + time + " sec");
			
			// clear panel
			for(int i = 0; i < 20; i++) {
				for(int j = 0; j < 20; j++) {
					if(map[i][j] == 0) {
						panels[i][j].setBackground(Color.LIGHT_GRAY);
					}
					// 먹이가 있다면
					else if(map[i][j] == 7) {
						panels[i][j].setBackground(Color.RED);
					}
				}
			}
			
			// 뱀 그리기
			int index = 0;
			for( XY xy : snake ) {
				// 뱀의 머리
				if(index == 0) {
					panels[xy.y][xy.x].setBackground(Color.BLACK);
				}
				// 뱀의 몸통과 꼬리
				else {
					panels[xy.y][xy.x].setBackground(Color.GREEN);
				}
				
				index++;
			}
		}	// updateUI 끝
		
		public void makeSnakeList() {
			snake.add(new XY(10, 10));	// 초기 뱀의 머리
			snake.add(new XY(9, 10));		// 초기 뱀의 몸통
			snake.add(new XY(8, 10));		// 초기 뱀의 꼬리
		}
		
		public void initUI() {
			this.setLayout(new BorderLayout());
			
			// 위쪽 : 점수, 시간 메세지 공간
			panelNorth = new JPanel();
			panelNorth.setPreferredSize(new Dimension(400, 100));
			panelNorth.setBackground(Color.GRAY);
			panelNorth.setLayout(new FlowLayout());
			
			labelTitle = new JLabel("Score : 0, Time : 0 sec");
			labelTitle.setPreferredSize(new Dimension(400, 50));
			labelTitle.setFont( new Font("TimesRoman", Font.BOLD, 20) );
			labelTitle.setForeground(Color.BLACK);
			labelTitle.setHorizontalAlignment(JLabel.CENTER);
			
			panelNorth.add( labelTitle );
			
			labelMsg = new JLabel("Eat Food !");
			labelMsg.setPreferredSize(new Dimension(400, 20));
			labelMsg.setFont( new Font("TimesRoman", Font.BOLD, 20) );
			labelMsg.setForeground(Color.YELLOW);
			labelMsg.setHorizontalAlignment(JLabel.CENTER);
			
			panelNorth.add( labelMsg );
			
			this.add( "North", panelNorth );
			
			// 센터 : 게임 공간
			panelCenter = new JPanel();
			panelCenter.setLayout(new GridLayout(20, 20));
			for(int i = 0; i < 20; i++) {
				for(int j = 0; j < 20; j++) {
					panels[i][j] = new JPanel();
					panels[i][j].setPreferredSize(new Dimension(20, 20));
					panels[i][j].setBackground(Color.WHITE);
					panelCenter.add(panels[i][j]);
				}
			}
			
			this.add( "Center", panelCenter );
			
			// 빈 공간을 없애줌
			this.pack();
		}
	}
	
	// main
	public static void main(String[] args) {
		new MyFrame("Snake Game");
	}
}
