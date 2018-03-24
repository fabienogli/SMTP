package database;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Dns {

    private List<String> domain;
    private String dns_sites = "src/database/dns_sites.txt";
    private HashMap<String, InetAddress> dns;

    public Dns() {
        this.domain = new ArrayList<>();
        this.dns = new HashMap<>();
    }

    public void getDnsFromDb() {
        try {
            FileReader fileReader = new FileReader(dns_sites);
            BufferedReader db = new BufferedReader(fileReader);
            String chaine;
            int i = 1;
            while ((chaine = db.readLine()) != null) {
                if (i > 1) {
                    this.domain.add(chaine);
                }
                i++;
            }
            db.close();

        } catch (FileNotFoundException e) {
            System.out.println("Le fichier est introuvable !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Dns get() {
        Dns dns = new Dns();
        dns.getDnsFromDb();
        return dns;
    }
    //accesseurs

    public List<String> getDomain() {
        return domain;
    }

    public void setDomain(List<String> domain) {
        this.domain = domain;
    }

    public HashMap<String, InetAddress> getDns() {
        return dns;
    }

    public void setDns(HashMap<String, InetAddress> dns) {
        this.dns = dns;
    }
}
