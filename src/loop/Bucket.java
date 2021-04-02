package loop;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import graph.*;

/**
 * A bucket implementation based on a hashtable and linkedlists.
 * Entries to buckets are keys to the hashtable containing a linked
 * list for each bucket. The number of buckets are not fixed, but can be added or deleted dynamically 
 * during runtime
 * Deletion removes both the linked list associated with the key and the key itself from hashtable
 * @author Peter Majorin
 */

public class Bucket{
    private Hashtable bucket;

    public Bucket(){
        bucket = new Hashtable();
    }


    /** 
     *  Appends an item at end of a list given by key, or then constructs a new list
     *  and insert item there if there is no bucket at given key 
     */ 
    public void addItem(String key, Object obj){
        if (bucket.get(key) == null){
            LinkedList lL = new LinkedList();
            bucket.put(key, lL);
        }
        LinkedList lL = (LinkedList)bucket.get(key);
        if (!lL.contains(obj)) // xxx should be removed, the buckets may contain duplicates -pgm 
            lL.add(obj);
        
    }

    /** insert a whole linked list at a time */
    public boolean addItemList(String key, LinkedList l){
        if (bucket.get(key) != null) return false;
        else { 
            bucket.put(key, l);
            return true;
            
        }
    }

    public LinkedList getEntry(String key){
        return (LinkedList)bucket.get(key);
    }


    public boolean deleteEntry(String key){
        LinkedList lL = (LinkedList)bucket.get(key);
        if (lL == null) return false;
        lL.clear(); // delete linkedlist associated with this key
        bucket.remove(key); //also remove the bucket key from hashtable
        return true;
    }

    public void deleteEmptyEntries(){
        Vector strings = new Vector();
        Enumeration e = bucket.keys();

        while (e.hasMoreElements()){
          String key = (String)e.nextElement();
          LinkedList lL = (LinkedList)bucket.get(key);
          if (lL.size() == 0) { System.out.println(" Empty: "); strings.add(key);}
        }
        
        e = strings.elements();

        while (e.hasMoreElements()){
         String key = (String)e.nextElement();
         bucket.remove(key);
        }
    }


    public Iterator listIterator(){
        return bucket.values().iterator();
    }
 
    public void printEntry(String key){
        LinkedList l = getEntry(key);
        if (l == null){ 
            System.out.println("Bucket: Empty entry");
            return;
        }
        ListIterator LL = l.listIterator();
          System.out.print("key: "+key+"\t\t");
        while (LL.hasNext()){
            Node node = (Node)(LL.next());
            System.out.print(node.getName()+" ");
        }
        System.out.println();
    }
    
    public int size(){
        int number = 0;
        Enumeration e = bucket.keys();
        while (e.hasMoreElements()){
            number++;
            String key = (String)e.nextElement();
        }
      return number;
    }

    public void printBucket(){
        Enumeration e = bucket.keys();
        System.out.println("Start of bucket");
        while (e.hasMoreElements()){
            String key = (String)e.nextElement();
            printEntry(key);
        }
        System.out.println("End of bucket");
    }
    
    public static void main (String[] args){
        Bucket b = new Bucket();
        b.addItem("ff", "foooo");
        b.addItem("ff", "khh");
        b.addItem("ffdf", "krhh");
        b.addItem("koh", "nfsdf");
        System.out.println("size:"+b.size());
        b.deleteEntry("koh");
        System.out.println("size:"+b.size());
        LinkedList l = (LinkedList)b.getEntry("ff");
        ListIterator LL = l.listIterator();
        while (LL.hasNext()){
            System.out.println(LL.next());
        }
    }
}
