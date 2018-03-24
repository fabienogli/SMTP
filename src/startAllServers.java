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
        serverSMTP.lancer();
        serverPop3.lancer();
    }
}
