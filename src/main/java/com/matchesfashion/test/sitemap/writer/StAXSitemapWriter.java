package com.matchesfashion.test.sitemap.writer;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.util.List;

public class StAXSitemapWriter implements SitemapWriter {

    private final XMLOutputFactory factory = XMLOutputFactory.newInstance();

    @Override
    public String write(List<String> links) {
        try {
            StringWriter stringWriter = new StringWriter();
            XMLStreamWriter writer = factory.createXMLStreamWriter(stringWriter);

            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeCharacters("\n");
            writer.writeStartElement("urlset");
            writer.writeAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");
            writer.writeCharacters("\n");

            for (String link : links) {
                writer.writeCharacters("\t");
                writer.writeStartElement("url");
                writer.writeStartElement("loc");
                writer.writeCharacters(link);
                writer.writeEndElement();
                writer.writeEndElement();
                writer.writeCharacters("\n");
            }
            writer.writeEndElement();
            writer.writeCharacters("\n");
            writer.writeEndDocument();

            return stringWriter.getBuffer().toString();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }
}
