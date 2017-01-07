package com.bupt.poirot.utils;

/**
 * Created by hui.chen on 2016/12/26.
 */
public class StrUtils {

    // http://www.semanticweb.org/traffic-ontology#福中路
    public static String prefix(String iri) {
        return iri.split("#")[0];
    }

    // http://www.semanticweb.org/traffic-ontology#福中路
    public static String prefixWithSpit(String iri) {
        return iri.split("#")[0] + "#";
    }

    // http://www.semanticweb.org/traffic-ontology#福中路
    public static String getName(String iri) {
        return iri.split("#")[1];
    }

    // <http://www.semanticweb.org/traffic-ontology#福中路>
    public static String removeMark(String iri) {
        return iri.substring(1, iri.length() - 1);
    }

    // <http://www.semanticweb.org/traffic-ontology#福中路> true
    // http://www.semanticweb.org/traffic-ontology#福中路 false
    public static boolean containsMark(String iri) {
        return iri.contains("<") || iri.contains(">");
    }

    public static String replaceSuffixName(String iri, String to) {
        return iri.split("#")[0] + "#" + to + ">";
    }
}
