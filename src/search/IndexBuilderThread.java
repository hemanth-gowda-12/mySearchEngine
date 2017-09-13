package search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chimera on 9/11/17.
 */
public class IndexBuilderThread implements Runnable{

        private Document doc;

        public IndexBuilderThread(Document doc){
            this.doc=doc;
        }

        @Override
        public void run()
        {
            Map posIndex=new HashMap<String,ArrayList<Integer>>();
            String[] tokens = doc.getBody().split(" ");
            String[] token_arr;
            ArrayList temp=new ArrayList<Integer>();
            String token;

            for(int i=0;i<tokens.length;i++)
            {
                token_arr =tokens[i].replaceAll("^\\W+|\\W+$|'","").split("-");
                if(token_arr.length>1)
                {
                    for(int j = 0; j< token_arr.length; j++)
                    {
                        if(posIndex.containsKey(token_arr[j]))
                        {
                            temp= (ArrayList) posIndex.get(token_arr[j]);
                            temp.add(i+j);
                            posIndex.put(token_arr[j],temp);
                        }
                        else
                        {
                            temp.clear();
                            temp.add(i+j);
                            posIndex.put(token_arr[j],temp);
                        }
                    }
                }
                token=String.join("",token_arr);
                if(posIndex.containsKey(token))
                {
                    temp = (ArrayList) posIndex.get(token);
                    temp.add(i);
                    posIndex.put(token,temp);
                }
                else {
                    temp.clear();
                    temp.add(i);
                    posIndex.put(token,temp);
                }
            }
        }
    }

