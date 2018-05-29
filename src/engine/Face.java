/*
 * This class is under construction
 */

package engine;

public class Face {
    private IdxGroup[] idxGroups = new IdxGroup[3];
    
    public Face(String v1, String v2, String v3) {
        idxGroups = new IdxGroup[3];
        idxGroups[0] = parseLine(v1);
    }
    
    private IdxGroup parseLine(String line) {
        IdxGroup idxGroup = new IdxGroup();
        
        String[] lineTokens = line.split("/");
        int length = lineTokens.length;
        idxGroup.idxPos = Integer.parseInt(lineTokens[0]) - 1;
        if (length > 1) {
            String textCoord = lineTokens[1];
            //idxGroup.idxTextCoord = textCoord.length() > 0 ?
        }
        
        return idxGroup;
    }
    
    protected static class IdxGroup {
        public static final int NO_VALUE = -1;
        
        public int idxPos;
        public int idxTextCoord;
        public int idxVecNormal;
        
        public IdxGroup() {
            idxPos = NO_VALUE;
            idxTextCoord = NO_VALUE;
            idxVecNormal = NO_VALUE;
        }
    }
}
