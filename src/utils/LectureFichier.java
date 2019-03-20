package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

/**
 * <b>LectureFichier est la classe permettant de lire des fichiers externes</b>
 * LectureFichier va lire les fichiers externes pour recupere les informations
 * necessaires a l'execution du programme
 */
public class LectureFichier {
	private BufferedReader fichier;
	private String line;
	private String nomZone;
	private Vector<String> infos;
	private int nbInfo;
	
	/**
	 *Constructeur LectureFichier
	 *<p>
	 *A la construction d'un objet LectureFichier, un nouveau fichier est cree et lu ligne par ligne
	 *</p>
	 *@param nom nom du fichier a lire
	 *@param nbInfo nombre d'informations a recuperer
	 */
	public LectureFichier(String nom, int nb) throws IOException {
		this.fichier = new BufferedReader(new FileReader("src/fichiers/"+nom+".txt"));
		this.line = fichier.readLine();
		this.line = this.correctLine(this.line);
		this.nomZone = this.line;
		this.nbInfo = nb;
	}
	
	/**
	 * <h1>Verifier que le fichier est Vide</h1>
	 *Cette methode retourne "vrai" si la ligne de l'objet courant a une valeur nulle.
	 */
	public boolean isEmpty() {
		return (this.line == null);
	}
	
	/**
	 * <h1>Recupere le nom de la zone</h1>
	 *Cette methode retourne le nom de la zone de l'objet courant.
	 */
	public String getName() {
		return this.nomZone;
	}
	
	/**
	 * <h1>Lecture du fichier</h1>
	 *Cette methode permet de lire toutes les lignes de fichier a l'aide d'une boucle.
	 */
	public Vector<String> getInfos() throws IOException{
		this.infos = new Vector<String>();
		for(int i = 0; i < this.nbInfo; i ++) {
			this.infos.add(i, this.line);
			this.line = fichier.readLine();
			this.line = this.correctLine(this.line);
			}
		if(!this.isEmpty()) {
			if(this.line.contentEquals("x") || this.line.contentEquals("y")) {
				for(int i = nbInfo ; i < nbInfo + 3; i++) {
					this.infos.add(i, this.line);
					this.line = fichier.readLine();
					this.line = this.correctLine(this.line);
					}
		
				}
			}
		this.nomZone = this.line;
		return this.infos;
		}
	
	/**
	 * <h1>Renommer les zones correctement</h1>
	 *Cette methode permet de lire le nom des zones qui figurent dans le fichier et de leur faire correspondre un nom complet pour un meilleur affichage.
	 */
	public String correctLine(String ln) {
		if(ln!=null) {
			ln = ln.replaceAll("\\P{Print}","");
			if(ln.contentEquals("adm")) ln = "les bureaux de l'administration";
			if(ln.contentEquals("dir")) ln = "le bureau du directeur";
			if(ln.contentEquals("gardien")) ln = "la maison du gardien";
			if(ln.contentEquals("guichet")) ln = "au guichet";
			if(ln.contentEquals("entree")) ln = "le hall d'entrée";
			if(ln.contentEquals("reserve")) ln = "la reserve";
			if(ln.contentEquals("attente")) ln = "la salle d'attente";
			if(ln.contentEquals("coffres")) ln = "la salle des coffres";
			if(ln.contentEquals("imprimerie")) ln = "la salle d'impression";
			if(ln.contentEquals("couloir")) ln = "le couloir";
	
			
		}

		return ln;
	}
	
	/**
	 * <h1>Fermeture du fichier</h1>
	 *Cette methode permet de forcer la fermeture du fichier.
	 */
	public void closeFile() throws IOException {
		this.fichier.close();
	}
}
