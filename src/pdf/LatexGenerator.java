package pdf;

import dhcp.dataTypes.DCHPMessage;
import helper.ArrayHelpers;

public class LatexGenerator {
  public static int CreateLatexFile(String fileName, String title, DCHPMessage message){
    return CreateLatexFile(fileName, title, message, message.serialize());
  }
  public static int CreateLatexFile(String fileName, String title, DCHPMessage message, byte[] rawData){
    
    String out = "\\documentclass{article}\r\n"+
                   "\\usepackage[utf8]{inputenc}\r\n"+
                   "\\title{"+title+"}\r\n"+
                   "\\author{Auto-generated file}\r\n"+
                   "\\date{\\today}\r\n"+
                   
                   "\\begin{document}\r\n"+
                     "\\maketitle\r\n"+
                     "Raw data:\\\\\r\n";
    
    int i =0;
    for (byte data: rawData){
      i++;
      out +=""+(data & 0xFF)+", ";
      if (i%4 == 0)
        out += " ";
    }
                     
                out +="\\begin{table}\r\n"+
                       "\\begin{tabular}{|l|l|l|l|}\r\n"+
                         "\\hline\r\n"+
                         " OP      & HTYPE & HLEN  & HOPS \\\\\r\n"+
                         ""+(message.getOp()& 0xFF)+"       & "+(message.getHtype()& 0xFF)+"      & "+(message.getHlen()& 0xFF)+"      & "+(message.getHops()& 0xFF)+"     \\\\ \\hline\r\n"+
                         "\\multicolumn{4}{|l|}{XID} \\\\\r\n"+
                         "\\multicolumn{4}{|l|}{"+ArrayHelpers.byteArrayToString(message.getXid())+" } \\\\ \\hline\r\n"+
                         "\\multicolumn{2}{|l|}{secs} & \\multicolumn{2}{l|}{flags} \\\\\r\n"+
                         "\\multicolumn{2}{|l|}{"+ArrayHelpers.byteArrayToString(message.getSecs())+" } & \\multicolumn{2}{l|}{"+ArrayHelpers.byteArrayToString(message.getFlags())+"} \\\\ \\hline\r\n"+
                         "\\multicolumn{4}{|l|}{CIADDR} \\\\\r\n"+
                         "\\multicolumn{4}{|l|}{"+message.getCiaddr()+" } \\\\ \\hline\r\n"+
                         "\\multicolumn{4}{|l|}{YIADDR} \\\\\r\n"+
                         "\\multicolumn{4}{|l|}{"+message.getYiaddr()+" } \\\\ \\hline\r\n"+
                         "\\multicolumn{4}{|l|}{SIADDR} \\\\\r\n"+
                         "\\multicolumn{4}{|l|}{"+message.getSiaddr()+" } \\\\ \\hline\r\n"+
                         "\\multicolumn{4}{|l|}{GIADDR} \\\\\r\n"+
                         "\\multicolumn{4}{|l|}{"+message.getGiaddr()+"} \\\\ \\hline\r\n"+
                         "\\multicolumn{4}{|l|}{CHADDR} \\\\\r\n"+
                         "\\multicolumn{4}{|l|}{"+message.getChaddr()+"} \\\\ \\hline\r\n"+
                         "\\multicolumn{4}{|l|}{COOKIE} \\\\\r\n"+
                         "\\multicolumn{4}{|l|}{"+ArrayHelpers.byteArrayToString(message.getMagicCookie())+"} \\\\ \\hline\r\n"+
                         "\\multicolumn{4}{|l|}{OPTIONS} \\\\\r\n"+
                         "\\multicolumn{4}{|l|}{"+message.getOptions()+"} \\\\ \\hline\r\n"+
                       "\\end{tabular}\r\n"+
                     "\\end{table}\r\n"+
                  "\\end{document}\r\n";
    
    System.out.println(out);
    return 0;
    
  }
}
