package jamsa.rcp.downloader.models;

import jamsa.rcp.downloader.utils.Md5Encrypt;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;

/**
 * 序列化测试
 * 
 * @author 朱杰
 * 
 */
public class SerializeTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		System.out.println(Md5Encrypt.MD5Encode("aaaaa"));
		System.out.println(Md5Encrypt.MD5Encode("aaaaa"));
		System.out.println(Md5Encrypt.MD5Encode("aaaaab"));
	}

	public static void ser() throws Exception {
		Category cat = new Category();
		cat.setName("Ser1");
		cat.setPath("aaa");

		FileOutputStream fos = new FileOutputStream("c:\\temp\\ser");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		cat.setName("Se");
		oos.writeObject(cat);
		// oos.close();
		//		
		// fos = new FileOutputStream("c:\\temp\\ser");
		// oos = new ObjectOutputStream(fos);
		cat.setName("Ser2");
		oos.writeObject(cat);
		oos.reset();
		oos.writeObject(cat);
		oos.close();
	}

	public static void deSer() throws Exception {
		FileInputStream fis = new FileInputStream("c:\\temp\\ser");
		ObjectInputStream ois = new ObjectInputStream(fis);
		Category obj1 = (Category) ois.readObject();
		System.out.println(obj1.getName());
		Category obj2 = (Category) ois.readObject();
		System.out.println(obj2.getName());
		System.out.println(obj1 == obj2);
		Category obj3 = (Category) ois.readObject();
		System.out.println(obj3.getName());
		System.out.println(obj3 == obj2);
		ois.close();
	}

}
