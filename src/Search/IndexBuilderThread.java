package Search;

import org.tartarus.snowball.ext.PorterStemmer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by chimera on 9/11/17.
 */
@SuppressWarnings("DefaultFileTemplate")
class IndexBuilderThread implements Runnable{

        private final Document doc;
        private final GlobalPosIndex globalPosIndex;
        private final int docId;
        private PorterStemmer stemmer;

    public IndexBuilderThread(GlobalPosIndex globalPosIndex, Document doc){
            this.doc=doc;
            this.docId=doc.getId();
            this.globalPosIndex=globalPosIndex;
            this.stemmer = new PorterStemmer();
        }

        private String stem(String input)
        {
            stemmer.setCurrent(input);
            stemmer.stem();
//            System.out.println(stemmer.getCurrent());
            return stemmer.getCurrent();
        }

        @Override
        public void run()
        {

            Map posIndex=new HashMap<String,ArrayList<Integer>>();

            String[] tokens = doc.getBody().split("\\s+");
            String[] token_arr;
            ArrayList tempList=new ArrayList<Integer>();
            ArrayList newList=new ArrayList<Integer>();
            String token;

            Integer i=0;


            for (String tempToken:tokens)
            {
                if(!tempToken.matches(".*?\\w.*"))
                {
//                    System.out.println(tempToken+"!!!!!!!!!!!!!!");
                    continue;
                }
                token_arr =tempToken.replaceAll("^\\W+|\\W+$|'","").split("\\s*-\\s*");
                if(token_arr.length>1)
                {
                    for(int j = 0; j< token_arr.length; j++)
                    {
                        token=stem(token_arr[j]);
                        if(posIndex.containsKey(token))
                        {
                            tempList= (ArrayList) posIndex.get(token);
                            tempList.add(i+j);
                        }
                        else
                        {
                            newList.clear();
                            newList.add(docId);
                            newList.add(i+j);
                            posIndex.put(token,newList.clone());
                        }
                    }
                }
                token=stem(String.join("",token_arr));

                if(posIndex.containsKey(token))
                {
                    tempList = (ArrayList) posIndex.get(token);
                    tempList.add(i);
                }
                else {
                    newList.clear();
                    newList.add(docId);
                    newList.add(i);
                    posIndex.put(token,newList.clone());
                }
                i++;
            }

            Map.Entry key_val;
            for (Object o : posIndex.entrySet()) {
                key_val = (Map.Entry) o;
                tempList = (ArrayList) key_val.getValue();

//                Debug
//                System.out.println(key_val.getKey());
//                for(int j=0;j<temp.size();j++)
//                {
//                    System.out.print(temp.get(j)+" ");
//                }
//                System.out.println();
                globalPosIndex.add((String) key_val.getKey(),(Integer[]) tempList.toArray(new Integer[tempList.size()]));
            }
        }
    }