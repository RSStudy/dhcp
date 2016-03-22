/**
 * 
 */
package dhcp.dataTypes;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import helper.ArrayHelpers;

/**
 * @author ing. R.J.H.M. Stevens
 *
 */
public class DHCPOptions{
  private int length = 0;;
  
  
  //TODO depending on the ammount of options change this to a hashmap
  private List<DHCPOption> optionList = new ArrayList<DHCPOption>();;
  
  public DHCPOptions(){
  }
  
  public DHCPOptions(byte[] options){
    length = 0;
    optionList = new ArrayList<DHCPOption>();
    DeSerialize(options);
  }
  
  public DHCPOptions(DHCPOptions clone) {
    this.optionList = (List<DHCPOption>) ((ArrayList<DHCPOption>)clone.optionList).clone();
    this.length = clone.length;
  }

  public void emptyOptions(){
    optionList  = new ArrayList<DHCPOption>();
    length = 0;
  }
  
  public int  getLength(){
    return this.length;
  }
  public void addOption(byte code){
    addOption(code, new byte[0]);
  }
  public void addOption(byte code, byte content){
    addOption(code, new byte[]{content});
  }
  public void addOption(byte code, byte[] content){
    if (content == null) content = new byte[0];
    optionList.add(new DHCPOption(code, content));
    length += (2 + content.length);
  }
  public List<DHCPOption> getOptionList(){
    return optionList;
    
  }
  public void DeSerialize(byte[] options){
    int i = 0;
    if (options.length < 2){
      System.out.println("The option field is incorrect");
      return;
    }
    byte code;
    byte length;
    byte[] content;
    while (i < options.length){
      code = options[i];
      if (code == 0 && i != 0)
        break;
      length = options[i+1];
      content = new byte[length];
      System.arraycopy(options, i+2, content, 0, length);
      
      addOption(code, content);
      i += (2+length);
      
    }
    
  }
  public byte[] Serialize(){
    try {
      ByteArrayOutputStream bOutSt = new ByteArrayOutputStream(length);
      DataOutputStream      outSt  = new DataOutputStream(bOutSt);
      for (DHCPOption option : optionList){
        outSt.writeByte  (option.code                              );
        outSt.writeByte  (option.content.length                    );
        outSt.write      (option.content,0, option.content.length  );
      }
      return  bOutSt.toByteArray();
    } catch (IOException e) {
      return new byte[0];
    }
  }
  
  public class DHCPOption{
    public DHCPOption(byte code, byte[] content){
      this.code     = code;
      this.content  = content;
    }
    public byte code;
    public byte[] content;
  }

  public byte[] getContentFromCode(byte code) {
    for (DHCPOption option : optionList){
      if (option.code == code)
        return option.content;
    }
    return null;
    
  }
  
  public String toString(){
    String out = "";
    for(DHCPOption option: optionList){
      out += "code: "+option.code+" Content: "+ArrayHelpers.byteArrayToString(option.content)+"\\\\\r\n";
    }
    return out;
  }
}