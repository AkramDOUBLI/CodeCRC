/**
 *Nom: DOUBLI
 *Prénom: Akram
 *Groupe: B2
  **/
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

public class CRCFrame extends JFrame {
    private JLabel text, motComplet, code, verifie;
    private JTextField t, t1, a;
    private JButton calculer, verifier, vider, quitter;
    private JComboBox<String> comboBox;
    private HashMap<String, String> values;
    private JPanel p, p2;

    private JScrollPane scrollPane;

    //Constructeur pour créer une fenêtre contient tous les composants
    public CRCFrame(){
        super("Code CRC");
        setSize(1100, 700);
        setResizable(false);
        init();
        //ajouter un JScroll dans la fenêtre
        scrollPane = new JScrollPane(p2);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scrollPane);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

    }

    //méthode init qui permet d'ajouter les panels retournés par les autres méthodes
    public void init(){
        add(parametre(), BorderLayout.NORTH);
        add(button(), BorderLayout.SOUTH);
        add(dessiner(), BorderLayout.CENTER);

    }

    //méthode qui retourne un panel contient les boutons dans le sud de la fenêtre
    public JPanel button(){
        JPanel p = new JPanel(new FlowLayout((FlowLayout.LEFT)));
        //Creation des boutton
        calculer = new JButton("Calcul CRC");
        verifier = new JButton("Vérification");
        vider = new JButton("Vider  ");
        quitter = new JButton("Quitter");
        p.add(calculer);
        p.add(verifier);
        p.add(vider);
        p.add(quitter);
        p.add(verifie);
        // ajouter les actions aux bouttons
        calculer.addActionListener(e -> {
            calculer.getModel().setPressed(true);
            verifie.setText("");
            if(!t.getText().isEmpty()){
                p2.revalidate();
                p2.repaint();
            }
        });
        verifier.addActionListener(e -> {
            verifier.getModel().setPressed(true);
            if(!t.getText().isEmpty()){
                p2.revalidate();
                p2.repaint();
            }
        });
        //vider les champs et tout ce qu'était dessiné
        vider.addActionListener(e -> {
            t.setText("");
            t1.setText("");
            a.setText("");
            verifie.setText("");
            p2.repaint();

        });
        quitter.addActionListener( ae -> System.exit(0));

        //changer la couleur de boutons
        JButton[] boutons = {calculer, verifier, vider, quitter};
        for (JButton bouton : boutons) {
            bouton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    bouton.setBackground(Color.MAGENTA);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    bouton.setBackground(UIManager.getColor("Button.background"));
                }
            });
        }
        return p;
    }
    //méthode paramètre qui retourne un panel contient les champs de saisie et une liste des polynomes générateurs
    public JPanel parametre(){
        p    = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(new Color(6, 141, 157));
        text = new JLabel("Entrez une chaine:");
        t    = new JTextField( 25);
        motComplet =new JLabel("La chaine complete :");
        a = new JTextField(20);
        code = new JLabel("Code CRC obtenu:");
        t1   = new JTextField(10);
        //les champs de la chaine complète et le code CRC obtenu sont non editable (pour afficher seulement)
        t1.setEditable(false);
        a.setEditable(false);
        //Cette variable sera utilisée pour afficher un message après la vérification d'une chaine
        verifie = new JLabel("");

        //changer la couleur de JLabel
        motComplet.setForeground(Color.CYAN);
        text.setForeground(Color.CYAN);
        code.setForeground(Color.CYAN);

        //Création d'un JComboBox avec les options
        String[] options = {"CRC-4", "CRC-12", "CRC-16", "CRC-CCITT", "CRC-32"};
        comboBox = new JComboBox<>(options);
        //Création d'un tableau de valeurs associées aux options
        values = new HashMap<>();
        values.put("CRC-4", "10110");
        values.put("CRC-12", "1100000001111");
        values.put("CRC-16", "11000000000000101");
        values.put("CRC-CCITT", "10001000000100001");
        values.put("CRC-32", "100000100110000010001110110110111");

        //permet d'entrer que 1 et 0 et de faire copier-coller et retour en arrière
        t.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == '1' || e.getKeyChar() == '0' || e.getKeyCode() == KeyEvent.VK_BACK_SPACE || (e.getModifiers() == KeyEvent.CTRL_MASK && (e.getKeyCode() == KeyEvent.VK_C || e.getKeyCode() == KeyEvent.VK_V))){
                    t.setEditable(true);
                } else {
                    t.setEditable(false);
                }
                System.out.println(t.getText());
            }
            @Override
            public void keyTyped(KeyEvent e) {
            }
            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        p.add(text);
        p.add(t);
        p.add(comboBox);
        p.add(motComplet);
        p.add(a);
        p.add(code);
        p.add(t1);

        return p;
    }
    //méthode dessiner qui permet de créer et retourner un panel de la classe interne divisionPanel
    public JPanel dessiner(){
        p2 = new DivisionPanel();
        p2.setPreferredSize(new Dimension(2000, 2000));
        return p2;

    }

    //Classe interne pour dessiner la division euclidienne pendant le calcul de CRC et la vérification d'une chaine
    class DivisionPanel extends JPanel {

        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            Font font = new Font("Arial", Font.BOLD, 15);
            g.setFont(font);

            //récupérer la chaine entrée et le polynome choisi
            String message = t.getText();
            String selectedOption = comboBox.getSelectedItem().toString();
            String generator = values.get(selectedOption);

            //la variable width sera utilisé pour tracer la ligne au-dessous de polygénérateur
            FontMetrics fm = g.getFontMetrics();
            Rectangle2D r = fm.getStringBounds(generator, g);
            int width = (int) r.getWidth();

            //Pour calculer le code CRC
            if (calculer.getModel().isPressed()) {
                //récupérer la chaine dans une autre variable
                String remainder = message;
                String t = "";
                //ajouter des 0 à la fin de la chaine ça dépend le degré du polynôme générateur
                int m = generator.length() - 1;
                for (int i = 0; i < m; i++) {
                    remainder = remainder + "0";
                }
                int j = 1;
                //enlever les 0 au debut de la chaine
                remainder = remainder.replaceFirst("^0+(?!$)", "");
                //tant que la langueur de la chaine est superieur ou égale la langeur du polynome générateur
                while (remainder.length() >= generator.length()) {
                    g.drawString(remainder, j * 50, j * 50);
                    g.drawString(generator, 50 * j, 50 * j + 20);
                    g.drawLine(50 * j, 50 * j + 20, 50 * j + width, 50 * j + 20);
                    j++;
                    //pour chaque itération, on vérifie si la position de bit courant de la chaine et le polynôme est le même on met 0 sinon 1
                    for (int i = 1; i < generator.length(); i++) {
                        if (remainder.charAt(i) == generator.charAt(i)) {
                            t += "0";
                        } else {
                            t += "1";
                        }
                    }
                    System.out.println("iteration :" + t);
                    //on ajoute à la chaine obtenue le reste de la chaine précédante
                    t += remainder.substring(generator.length());
                    remainder = t;
                    remainder = remainder.replaceFirst("^0+(?!$)", "");
                    t = "";
                }
                //ajouter de 0 à droite pour que la langueur de CRC soit le degré de polynome générateur
                String reslutCRC = remainder;
                if(reslutCRC.length() < generator.length() - 1){
                    while (reslutCRC.length() < generator.length() - 1){
                        reslutCRC = "0" + reslutCRC;
                    }
                }
                //on affiche la chaine complète et le code CRC obtenu dans le TextField a et t1
                a.setText(message + reslutCRC);
                t1.setText(reslutCRC);
                g.setColor(Color.MAGENTA);
                g.drawString(reslutCRC, j * 50, j * 50);
                System.out.println("Le code CRC est : " + reslutCRC);

                calculer.getModel().setPressed(false);

            }



            //Pour vérifier le code CRC
            else if (verifier.getModel().isPressed()){
                String t = "";
                //on récupère la chaine complete et s'il est vide on prend le message entré par l'utilisateur
                String remainder = a.getText();
                if (remainder.isEmpty()){
                    remainder = message;
                }
                int j = 1;
                remainder = remainder.replaceFirst("^0+(?!$)", "");
                while (remainder.length() >= generator.length()) {
                    g.drawString(remainder, j * 50, j * 50);
                    g.drawString(generator, 50 * j, 50 * j + 20);
                    g.drawLine(50 * j, 50 * j + 20, 50 * j + width, 50 * j + 20);
                    j++;
                    for (int i = 1; i < generator.length(); i++) {
                        if (remainder.charAt(i) == generator.charAt(i)) {
                            t += "0";
                        } else {
                            t += "1";
                        }
                    }
                    System.out.println("iteration :" + t);
                    t += remainder.substring(generator.length());
                    remainder = t;
                    remainder = remainder.replaceFirst("^0+(?!$)", "");
                    t = "";
                }

                //ajouter de 0 à droite
                String resultat = remainder;
                if(resultat.length() < generator.length() - 1){
                    while (resultat.length() < generator.length() - 1){
                        resultat = "0" + resultat;
                    }
                }

                //verifier si le reste contient au moins un bit différent de 0, alors le message contient des erreurs sinon le contraire
                if (resultat.contains("1")){
                    g.setColor(Color.RED);
                    verifie.setForeground(Color.RED);
                    verifie.setFont(new Font("Arial", Font.BOLD, 20));
                    System.out.println("Le resultat est : " + resultat);
                    verifie.setText("Cette chaine  contient des erreurs!");
                }
                else {
                    g.setColor(Color.GREEN);
                    verifie.setForeground(Color.GREEN);
                    verifie.setFont(new Font("Arial", Font.BOLD, 20));
                    System.out.println("Le resultat est : " + resultat);
                    verifie.setText("Cette chaine ne contient pas d'erreur!");
                }
                //dessiner le resultat
                g.drawString(resultat, j * 50, j * 50);

                verifier.getModel().setPressed(false);

            }
        }
    }
    public static void main(String[] args){
        SwingUtilities.invokeLater(CRCFrame::new);
    }
}