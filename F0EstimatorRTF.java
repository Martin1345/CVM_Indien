import java.io.FileInputStream; 
// Importierung der Klasse zum Lesen von Dateien
import java.util.*; 
// Impportierung der Java Collections Framework Klassen
import javax.swing.text.Document; 
// Importierung der Document-Klasse für Textverarbeitung
import javax.swing.text.rtf.RTFEditorKit; 
// Importierung der RTFEditorKit-Klasse zum Einlesen von RTF-Dateien

public class F0EstimatorRTF {

    public static void main(String[] args) throws Exception {
        //  Einstellung der Parameter für die Schätzung des F₀-Werts
        double Epsilon = 0.005; 
        // Tollerierter Fehler bei der Schätzung
        double Delta = 0.005;  
        // Wahrscheinlichkeit, dass die Schätzung außerhalb des Toleranzbereichs liegt
        int Durchlaeufe = 1000; 
        // Anzahl der Durchläufe für die Schätzung

        //  RTF-Datei einlesen
        FileInputStream fis = new FileInputStream("C:\\Users\\marti\\Desktop\\Datensätze\\Hotel.rtf"); 
        // Pfad zur Datei, die eingelesen werden soll
        RTFEditorKit rtfKit = new RTFEditorKit(); 
        // Einlesung der Daei mittels RTFEditorKit
        Document doc = rtfKit.createDefaultDocument(); 
        // Erstellen eines neuen 
        //Dokuments für den RTF-Inhalt
        rtfKit.read(fis, doc, 0); // Einlesen des RTF-Inhalts in das Dokument
        fis.close(); // InputStream nach erfolgreichem Einlesen schließen

        String Volltext = doc.getText(0, doc.getLength()); 
        // gesamten Text des Dokuments als String extrahieren
        String[] Zeilenarray = Volltext.split("\\r?\\n"); 
        // Aufteilen des Textes in  Zeilen anhand von Zeilenumbrüchen
        List<String> Zeilen = new ArrayList<>(); // Liste, die 
        //die Zeileninhalte speichert
        for (String Zeile : Zeilenarray) { // Durchlauf aller Zeilen in der Datei
            String gekürzt = Zeile.trim(); // Entferung von führenden
            //und nachfolgenden Leerzeichen
            if (!gekürzt.isEmpty()) { 
                // Prüfung, ob die Zeile nicht leer ist
                Zeilen.add(gekürzt); 
                // Falls ja, hinzufügen der Zeile zur Liste
            }
        }

        int m = Zeilen.size(); 
        // Variable für die Anzahl der eingelesenen Zeilen
        int tatsächlich = new HashSet<>(Zeilen).size(); 
        // Tatsächliche Anzahl unterschiedlicher Zeilen
        System.out.printf(" Zeilen gelesen: %d\n Epsylon= %.4f | Delta = %.4f | Durchlaeufe = %d\n", m, Epsilon, Delta, Durchlaeufe); 
        // Ausgabe der Parameter und Anzahl eingelesener Zeilen an den Nutzer

        List<Double> Schätzungen = new ArrayList<>(); 
        // Eigene Liste für die Schätzungen F₀
        List<Long> Speicherverbrauch = new ArrayList<>(); 
        // Liste für den Speicherplatzverbrauch der Schätzungen
        long LaufzeitGesamt = 0; 
        // Gesamtlaufzeit aller Durchläufe in Nanosekunden
        int erfolgreicheDurchläufe = 0; 
        // Anzahl erfolgreicher Durchläufe

        for (int Durchlauf = 1; Durchlauf <= Durchlaeufe; Durchlauf++) { 
            // Angegebenen Anzahl an Durchläufen wird durchlaufen
            long Zeit_Start = System.nanoTime(); 
            // Zeit beim Start des Durchlaufs erfassen

            int Schwelle = (int) Math.ceil((12 / Math.pow(Epsilon, 2)) * Math.log(8.0 * m / Delta)); 
            // Berechnung des Schwellwertes
            //nach Formel aus der Aufgabenstellung
            double p = 1.0; 
            // Wahrscheinlichkeit für die Auswahl einer 
            //Zeile wird zu Beginn auf 1 gesetzt
            Set<String> X = new HashSet<>(); 
            // Menge X, die die ausgewählten Zeilen speichert
            boolean fehlgeschlagen = false; 
            // Flag, das angibt, ob der Durchlauf fehlgeschlagen ist

            for (String Zeile : Zeilen) { 
                // Durchlauf aller Zeilen in der Liste
                if (Math.random() < p) { 
                    // Zufälliges hinzufügen der Zeile zu X basierend
                    //auf Zufallszahl und Wahrscheinlichkeit p
                    X.add(Zeile);
                    // Falls die Zufallszahl kleiner als p ist, 
                    //wird die Zeile zur Menge X hinzugefügt
                }
                if (X.size() == Schwelle) { 
                    // Prüfung, ob die maximale Anzahl an ausgewählten
                    //Zeilen erreicht wurde
                    Set<String> reduziert = new HashSet<>();
                    // Reduzierung der maximalen Anzahl an Elementen auf die Hälfte
                    for (String x : X) { // Durchlauf aller Elemente von X um eventuell Elemente zu entfernen
                        if (Math.random() < 0.5) { 
                            // Neue Zufallszahl für jedes Element soll diese kleiner
                            //als 0.5 sein, wird das Element behalten
                            reduziert.add(x); 
                            // Element wird zur reduzierten Menge hinzugefügt
                        }
                    }
                    X = reduziert; 
                    // Die maximale Anzahl an Elementen in X 
                    //wird auf die reduzierte Menge gesetzt
                    p /= 2.0; 
                    // Die Wahrscheinlichkeit p wird halbiert
                    //,um die Schätzung zu verfeinern
                    if (X.size() == Schwelle) { 
                        // Prüfung, ob die reduzierte Meng immer
                        //noch zu viele Elemente enthält
                        fehlgeschlagen = true;  
                        //Falls ja, wird der Durchlauf als fehlgeschlagen markiert
                        break; 
                        // Abbruch des aktuellen Durchlaufs, 
                        //da die Schätzung nicht erfolgreich war
                    }
                }
            }

            long Dauer = System.nanoTime() - Zeit_Start; 
            // Berechnung der Laufzeit des aktuellen Durchlaufs

            if (!fehlgeschlagen) { 
                // Prüfung, ob der Durchlauf erfolgreich war
                double Schätzwert = X.size() / p; 
                // Berechnung des Schätzwertes F₀ basierend auf der Größe 
                //von X und der Wahrscheinlichkeit p
                Schätzungen.add(Schätzwert); 
                //Hinzufügen des Schätzwertes zur Liste der Schätzungen
                LaufzeitGesamt += Dauer; 
                // Hinzufügen der Laufzeit des 
                //aktuellen Durchlaufs zur Gesamtlaufzeit
                erfolgreicheDurchläufe++; 
                // Erhöhung des Zählers für erfolgreiche Durchläufe

                
                int zeichenSumme = 0;
                // Variable zur Berechnung des Speicherverbrauchs in Bytes
                for (String s : X) {
                    zeichenSumme += s.length();
                    // Länge der Zeichenkette wird zur Summe hinzugefügt
                }
                long speicherX = zeichenSumme * 2; 
                // Annahme: 2 Bytes pro Zeichen (UTF-16)
                Speicherverbrauch.add(speicherX); 
                // Hinzufügen des Speicherverbrauchsder 
                //Menge X zur Liste des Speicherverbrauchs
            }
        }

        //  Durchschnittswerte berechnen
        double Durchschnitt_Schätz = Schätzungen.stream().mapToDouble(Double::doubleValue).average().orElse(0.0); 
        // Berechnung der duchschnittlichen 
        //Schätzung F₀
        double Durchschnitt_Zeit = erfolgreicheDurchläufe > 0 ? LaufzeitGesamt / erfolgreicheDurchläufe / 1e6 : 0.0; 
        // Berechnung der durchschnittlichen
        //Laufzeit in ms
        double Durchschnitt_Speicher = Speicherverbrauch.stream().mapToLong(Long::longValue).average().orElse(0L) / 1024.0; 
        // Berechnung des durchschnittlichen 
        //Speicherverbrauchs in KB
        double Abweichung = tatsächlich > 0 ? Math.abs(Durchschnitt_Schätz - tatsächlich) / tatsächlich * 100 : 0.0; 
        //  Berechnung der prozentualen Abweichung zur 
        //tatsächlichen Anzahl der unterschiedlichen Zeilen

        //  Ausgabe der Ergebnisse
        System.out.printf("%n Erfolgreiche Durchlaeufe: %d von %d%n", erfolgreicheDurchläufe, Durchlaeufe); 
        // Ausgabe der erfolgreichen Durchläufe
        System.out.printf(" Tatsächliche Anzahl unterschiedlicher Zeilen: %d%n", tatsächlich); 
        // Ausgabe der tatsächlichen Anzahl unterschiedlicher Zeilen
        System.out.printf(" Durchschnittliche Schaetzung: %.0f%n", Durchschnitt_Schätz); 
        // Ausgabe der durchschnittlichen Schätzung F₀
        System.out.printf(" Durchschnittliche Abweichung: %.2f%%%n", Abweichung); 
        // Ausgabe der prozentualen Abweichung zur 
        //tatsächlichen Anzahl unterschiedlicher Zeilen
        System.out.printf(" Durchschnittliche Laufzeit: %.2f ms%n", Durchschnitt_Zeit); 
        // Ausgabe der durchschnittlichen Laufzeit in Millisekunden
        System.out.printf(" Gesamtlaufzeit aller Durchlaeufe: %.2f s%n", LaufzeitGesamt / 1e9); 
        // Ausgabe der Gesamtlaufzeit aller Durchläufe in Sekunden
        System.out.printf(" Durchschnittlicher Speicherverbrauch: %.0f KB%n", Durchschnitt_Speicher); 
        // Ausgabe des durchschnittlichen 
        //Speicherverbrauchs in Kilobyte    }
}
