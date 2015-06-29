package tile;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Mapa extends JFrame implements Runnable {

	private BufferedImage tileSet;
	public static BufferedImage mapa;

	private int tileSize = 32;
	private int rowTileCount = 20;
	private int colTileCount = 20;
	private int imageNumTiles = 8;

	public int camada0[][] = new int[rowTileCount][colTileCount];
	public int camada1[][] = new int[rowTileCount][colTileCount];
	public int camada2[][] = new int[rowTileCount][colTileCount];

	public Dimension dimTela;

	public int[][] carregaMatriz(String diretorio) {

		ArrayList<String> arqText = new ArrayList<String>();
		InputStream is = getClass().getResourceAsStream(diretorio);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String linha = "";
		int matz[][] = new int[rowTileCount][colTileCount];
		try {

			while ((linha = br.readLine()) != null)
				arqText.add(linha);

			int j = 0;
			for (int i = 0; i < arqText.size(); i++) {
				StringTokenizer tokens = new StringTokenizer(arqText.get(i),
						",");
				while (tokens.hasMoreElements()) {
					matz[i][j] = Integer
							.parseInt((String) tokens.nextElement());
					j++;
				}
				j = 0;
			}
		} catch (Exception e) {
			System.out.println("nao carregou");
			System.exit(0);
		}
		return matz;
	}

	public void run() {
		mapa = new BufferedImage(640, 640, BufferedImage.TYPE_4BYTE_ABGR);
		camada0 = carregaMatriz("mapa1.txt");
		camada1 = carregaMatriz("mapa2.txt");
		camada2 = carregaMatriz("mapa3.txt");
		montarMapa(camada0);
		montarMapa(camada1);
		montarMapa(camada2);
		while (true) {
			try {
				// repaint();
				desenha();
				Thread.sleep(1000 / 1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void desenha() {
		Graphics2D g2d = (Graphics2D) this.getGraphics();
		g2d.drawImage(mapa.getScaledInstance(dimTela.width, dimTela.height,
				Image.SCALE_DEFAULT), 0, 0, null);
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(mapa, 0, 0, null);
	}

	public Mapa(String img) {

		dimTela = Toolkit.getDefaultToolkit().getScreenSize();

		setSize(dimTela.width, dimTela.height);
		setPreferredSize(new Dimension(dimTela.width, dimTela.height));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setUndecorated(true);

		try {
			tileSet = ImageIO.read(this.getClass().getClassLoader()
					.getResource(img));
		} catch (IOException e) {
			System.out
					.println("Erro ao buscar imagem do mapa.\nEncerrando aplicação");
			System.exit(0);
		}
		setVisible(true);
	}

	public void montarMapa(int[][] matriz) {

		for (int i = 0; i < rowTileCount; i++) {
			for (int j = 0; j < colTileCount; j++) {
				int tile = (matriz[i][j] != 0) ? (matriz[i][j] - 1) : 0;
				int tileRow = (tile / imageNumTiles) | 0;
				int tileCol = (tile % imageNumTiles) | 0;
				mapa.getGraphics().drawImage(tileSet, (j * tileSize),
						(i * tileSize), (j * tileSize) + tileSize,
						(i * tileSize) + tileSize, (tileCol * tileSize),
						(tileRow * tileSize), (tileCol * tileSize) + tileSize,
						(tileRow * tileSize) + tileSize, null);
			}
		}
	}

	public static void main(String[] args) {
		new Thread(new Mapa("TileSet.png")).start();
	}
}
