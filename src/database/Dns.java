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

    public void associateNameIp(String name, String string_ip) throws UnknownHostException, NoNameDnsException {
        InetAddress ip = InetAddress.getByName(string_ip);
        if (!this.getDomain().contains(name)) {
            throw new NoNameDnsException("Pas de nom associé à " + name);
        }
        for (String tmp : this.getDomain()) {
            if (tmp.equals(name)) {
                this.dns.put(name, ip);
                return;
            }
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

    @Override
    public String toString() {
        StringBuilder toString = new StringBuilder();
        int i = 0;
        for (String name : domain) {
            toString.append(name)
                    .append(" : ");
            if (dns.containsKey(name)) {
                toString.append(dns.get(name));
            } else {
                toString.append("libre, channel ")
                        .append(i);
                i++;
            }
            toString.append("\n");
        }
        return toString.toString();
    }
}
