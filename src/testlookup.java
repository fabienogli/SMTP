///*
// * Copyright (c) 2018. Mark KPAMY -Fabien OGLI - Florian LOMBARDO
// */
//
//
//import org.xbill.DNS.Lookup;
//import org.xbill.DNS.Record;
//import org.xbill.DNS.TextParseException;
//import org.xbill.DNS.Type;
//
//public class testlookup {
//
//    public static void main(String[] args) {
//
//        try {
//            Record[] records = lookupMxRecords("univ-lyon1.fr");
//            for(int i=0;i<records.length;i++){
//            System.out.println(records[i].toString());
//            }
//        } catch (TextParseException e) {
//            e.printStackTrace();
//        }
//
//
//    }
//    private static Record[] lookupMxRecords(final String domainPart) throws TextParseException //throw TextParseException
//    {
//        final Lookup dnsLookup = new Lookup(domainPart, Type.MX);
//        return dnsLookup.run();
//    }
//
//
//}
