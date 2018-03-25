/*
 * Copyright (c) 2018. Mark KPAMY -Fabien OGLI - Florian LOMBARDO
 */

import pop3.server.ServerPop3;
import smtp.server.ServerSMTP;

public class startAllServers {

    public static void main(String[] args) {
        ServerSMTP serverSMTP = new ServerSMTP();
        ServerPop3 serverPop3 = new ServerPop3();
        //registerDns(serverSMTP.getDns());
        Thread t = new Thread(serverSMTP);
        Thread t2 = new Thread(serverPop3);
        t.start();
        t2.start();

    }
}
