package GUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

/**
 * Displays the game world to the user. Displays in three level,
 * player layer, item layer, tile layer. 
 * Extends JLayeredPane to be able to display images on top of each other.
 * @author charlie
 *
 */
public class DisplayGrid extends JLayeredPane{
	private static final long serialVersionUID = 2914784780316609033L;
	private final static String TILE_PATH = "res" + File.separator + "tile" + File.separator;
	private final static String ITEM_PATH = "res" + File.separator + "item" + File.separator;
	//player paths
	private final static String GOBLIN_PATH = "res" + File.separator + "player" + File.separator+ "goblin";
	private final static String SKELETON_PATH = "res" + File.separator + "player" + File.separator+ "skeleton";
	private final static String HUMAN_PATH = "res" + File.separator + "player" + File.separator+ "human";
	private final static String PLAYER_N = "_north.png";
	private final static String PLAYER_E = "_east.png";
	private final static String PLAYER_S = "_south.png";
	private final static String PLAYER_W = "_west.png";
	
	private final static String WIN_PATH = "res" + File.separator + "misc" + File.separator+ "win.png";
	private final static String LOSE_PATH = "res" + File.separator + "misc" + File.separator+ "lose.png";
	
	private final static String SHADOW_PATH = "res" + File.separator + "misc" + File.separator+ "shadow.png";
	//char
	public final static char WALL = '#';
	public final static char FLOOR = '.';
	public final static char PLAYER = 'P';
	public final static char GOLD = 'G';
	public final static char LANTERN = 'L';
	public final static char SWORD = 'S';
	public final static char ARMOUR = 'A';
	public final static char HEALTH = 'H';
	public final static char EXIT = 'E';
	public final static char UNKNOWN = '?';
	public final static char OUT_OF_VIEW = 'X';
	
	private final int TILE_SIZE = 80;//default size
	private double tile_multiplier = 1.0;//ratio for resizing graphics
	private int gridSize = 5;
	private boolean gameOver = false;
	private boolean win = false;
	
	public DisplayGrid()
	{
		this.setPreferredSize(new Dimension(400,400));
		this.setLayout(new GridBagLayout());
		this.setBackground(Color.black);
	}
	
	/**
	 * refresh the graphics on screen.
	 */
	public void refreshScreen()
	{
		this.revalidate();
		this.repaint();
	}
	
	/**
	 * updates the display on screen replacing what was previously on screen.
	 * 
	 * @param playerLayer render hints for the player is a string of hints
	 * @param itemLayer
	 * @param tileLayer
	 */
	private void updateDisplay(String[][][] playerLayer, char[][] itemLayer, char[][] tileLayer)
	{
		gridSize = playerLayer[0].length;
		GridBagConstraints c = new GridBagConstraints();
		
		//check if lantern has been picked up or not. 
		if(gridSize == 5){
			tile_multiplier = 1.0;
		}else if(gridSize == 7){
			tile_multiplier = 5.0/7.0;//ratio to multiply by
		}
		
		this.removeAll();//remove all current graphics from screen.
		
		if(gameOver){
			//displays game end result
			this.displayGameOver(win);
		}
		//shadow
		this.drawShadowOverlay(c, gridSize);
		c.gridheight = 1;
		c.gridwidth = 1;
		this.drawMap(c, tileLayer, itemLayer, playerLayer);
		//make sure screen is re-drawn;
		this.refreshScreen();
	}
	

	/**
	 * draws a shadow on the top of the game.
	 * 
	 * @param c GridBagConstraints
	 * @param size the size to makes the gridBagConstraints match
	 */
	private void drawShadowOverlay(GridBagConstraints c, int size)
	{
		JLabel label;
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = size;
		c.gridheight = size;
		label = new JLabel();
		label.setIcon(new ImageIcon(SHADOW_PATH));
		this.add(label,c);
	}
	
	/**
	 * Draws a player to screen based on provided renderhints and its position 
	 * @param c GridBagConstraints
	 * @param player
	 */
	private void drawPlayer(GridBagConstraints c, String[] player)
	{
		JLabel label;
		
		//player name
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.NORTH;
		label = new JLabel();
		label.setText(player[2].toString().toUpperCase());
		label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
		label.setForeground(Color.green);
		this.add(label,c);
		
		//draw player
		c.anchor = GridBagConstraints.CENTER;
		drawToTile(c, this.getPlayerImagePath(player[0], player[1]));
	}
	
	/**
	 * Handles drawing all three layers of the gameView (Player layer, item layer, tileLayer
	 * @param c GridBagConstraints
	 * @param grid 
	 * @param itemLayer
	 * @param playerLayer
	 */
	private void drawMap(GridBagConstraints c, char[][] tileLayer, char[][] itemLayer, String[][][] playerLayer){
		for(int i = 0; i < tileLayer.length; i++){
			for(int j = 0; j < tileLayer[i].length; j++){
				c.gridx = j;
				c.gridy = i;
				
				String[] player = playerLayer[i][j];
				if(player[0]!= null){//has renderhint
					this.drawPlayer(c, player);
				}
				
				String itemPath = this.getItemImagePath(itemLayer[i][j]);
				if(itemPath!=""){//has character
				drawToTile(c, itemPath);
				}
				
				drawToTile(c, getTileImagePath(tileLayer[i][j]));
			}
		}
		
	}
	
	/**
	 * This is where the drawing for elements on the map takes place. Image is
	 * resized and added to the map.
	 * 
	 * @param c GridBagConstraints
	 * @param path image path
	 */
	private void drawToTile(GridBagConstraints c, String path){
		JLabel label = new JLabel();
		File imgPath = new File(path);
		BufferedImage img = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_RGB);
		try {
			img = ImageIO.read(imgPath);
		} catch (IOException e) {
		}
		label.setIcon(new ImageIcon(this.resize(img)));
		this.add(label,c);
	}
	
	/**
	 * gets the image path for a player based on its type and direction
	 * @param type human/goblin/skeleton
	 * @param lastDirection direction facing
	 * @return
	 */
	private String getPlayerImagePath(String type, String direction)
	{
		String imgPath = "";
		//type
		if(type.equals("HUMAN")){
			imgPath = imgPath + HUMAN_PATH;
		}else if(type.equals("GOBLIN")){
			imgPath = imgPath + GOBLIN_PATH;
		}else{
			imgPath = imgPath + SKELETON_PATH;
		}
		
		//direction
		if(direction.equals("N")){
			return imgPath+PLAYER_N;
		}else if(direction.equals("E")){
			return imgPath+PLAYER_E;
		}else if(direction.equals("W")){
			return imgPath+PLAYER_W;
		}else{
			return imgPath+PLAYER_S;
		}
	}
	
	/**
	 * get the image path of an item based on its type
	 * @param type
	 * @return
	 */
	private String getItemImagePath(char type)
	{
		switch(type){
			case GOLD:
				return ITEM_PATH + "gold.png";
			case PLAYER:
				return TILE_PATH + "floor_tile.png";
			case LANTERN:
				return ITEM_PATH + "lantern.png";
			case SWORD:
				return ITEM_PATH + "sword.png";
			case ARMOUR:
				return ITEM_PATH + "armour.png";
			case HEALTH:
				return ITEM_PATH + "health.png";
			default:
				return "";
		}
	}
	
	/**
	 *  get the image path of a tile based on its type
	 * @param type
	 * @return
	 */
	private String getTileImagePath(char type)
	{
		switch(type){
			case FLOOR:
				return TILE_PATH + "floor_tile.png";
			case WALL:
				return TILE_PATH + "wall_tile.png";
			case EXIT:
				return TILE_PATH + "exit_tile.png";
			case OUT_OF_VIEW:
				return TILE_PATH + "outofview_tile.png";
			default:
				return TILE_PATH + "unknown_tile.png"; 
		}
	}
	
	//http://stackoverflow.com/questions/14548808/scale-the-imageicon-automatically-to-label-size
	/**
	 * Resizes an image based on the current multiplier ratio (default = 1)
	 * @param image the current image
	 * @return new resized image
	 */
	public BufferedImage resize(BufferedImage image) {
		//new width and height based on multiplier ratio
		int width = (int) ((double)image.getWidth() * tile_multiplier);
		int height = (int) ((double)image.getHeight() * tile_multiplier);
		
		//creates new scaled image
	    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
	    Graphics2D g2d = (Graphics2D) bi.createGraphics();
	    g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
	    g2d.drawImage(image, 0, 0, width, height, null);
	    g2d.dispose();
	    return bi;
	}
	/**
	 * Handles the LOOKREPLY from server.
	 * 
	 * @param lines the lines returned as part of the LOOKREPLY
	 * @throws IOException
	 */
	protected void handleLookReply(String[] lines) throws IOException {
		final int lookReplySize = lines[0].length();
		char[][] currentLookReply = new char[lookReplySize][lookReplySize];
		
		//Processing the LOOKREPLY
		for (int row = 0; row < lookReplySize; row++) {
			for (int col = 0; col < lookReplySize; col++) {
				char c = lines[row].charAt(col);
				currentLookReply[row][col] = c;
			}
		}
		this.render(currentLookReply, Arrays.copyOfRange(lines, lookReplySize, lines.length-1));
	}
	
	/**
	 * Handles the RENDERHINTS, creates default render hints if server is
	 * not sending renderHints.
	 * 
	 * @param lookReply latest look reply from server.
	 * @throws IOException
	 */
	private void render(char[][] lookReply, String[] hints) throws IOException{
		int length = lookReply.length;
		//3 layers to rendering player, items, tile.
		String[][][] playerLayer = new String[length][length][3];
		char[][] itemLayer = new char[length][length];
		char[][] tileLayer = new char[length][length];
		//setup default for player layer and row layer
		playerLayer[length/2][length/2][0] = "HUMAN";
		playerLayer[length/2][length/2][1] = "S";
		playerLayer[length/2][length/2][2] = "PLAYER";
		
		//default hints!
		for(char[] row: tileLayer){
			Arrays.fill(row, '.');//default, all floor
		}
		
		//create default renderHints;
		for (int row = 0; row < length; row++) {
			for (int col = 0; col < length; col++) {
				char c = lookReply[row][col];
				if(c == 'P'){
					//default  render hints for player layer
					playerLayer[row][col][0] = "HUMAN";
					playerLayer[row][col][1] = "S";
					playerLayer[row][col][2] = "";
					
				}
				else if(c == 'L' || c == 'H' || c == 'A' || c == 'S' || c == 'G'){
					itemLayer[row][col] = c;
				}else
				{
					tileLayer[row][col] = c;
				}
			}
		}
		
		
		//process the renderHints
		for(String line: hints){
				
			String[] s = line.split(" ");
			int col = Integer.parseInt(s[1].toString());
			int row = Integer.parseInt(s[2].toString());
					
			for(int i  = 3; i < s.length; i++){
				if(s[i].equals("PLAYERTYPE")){	
					playerLayer[row][col][0] = s[i+1].toString();
					playerLayer[row][col][1] = s[i+2].toString();
					playerLayer[row][col][2] = s[i+3].toString();
								
				}else if(s[i].equals("TILETYPE")){
					tileLayer[row][col] = s[i+1].charAt(0);
				}else if(s[i].equals("ITEMTYPE")){
					itemLayer[row][col] = s[i+1].charAt(0);
				}
			}
		}
		
		updateDisplay(playerLayer, itemLayer, tileLayer);
	}
	
	/**
	 * displays a message on top of the map showing the result of the game.
	 * @param win whether the player won or lost.
	 */
	public void displayGameOver(boolean win){
		this.gameOver = true;
		this.win = win;
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = gridSize;//fill whole panel.
		c.gridheight = gridSize;
		c.gridx = 0;
		c.gridy = 0;
		JLabel label = new JLabel();
		label.setBackground(null);
		if(win){
			label.setIcon(new ImageIcon(WIN_PATH));
		}else{
			label.setIcon(new ImageIcon(LOSE_PATH));
		}
		this.add(label,c, 0);
		this.refreshScreen();
	}
}
