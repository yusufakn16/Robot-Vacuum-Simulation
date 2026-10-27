package com.example.bz214proje.model;

import java.util.*;

public class PathFinder {
    public List<Position> findPath(Position start, Position target, RoomGrid grid){
        if(start.getX()==target.getX() && start.getY()==target.getY()){
            return new ArrayList<>();
        }

        Queue<Position> queue=new LinkedList<>();
        boolean[][] visited=new boolean[grid.getWidth()][grid.getHeight()];
        Map<Position, Position> parentMap=new HashMap<>();
        queue.add(start);
        visited[start.getX()][start.getY()]=true;
        boolean found=false;
        int[] dx={0,0,1,-1};
        int[] dy={-1,1,0,0};

        while (!queue.isEmpty()){
            Position current=queue.poll();

            if(current.getX()==target.getX() && current.getY()==target.getY()){
                found=true;
                break;
            }

            for(int i=0;i<4;i++){
                int nextX=current.getX()+dx[i];
                int nextY=current.getY()+dy[i];

                if(grid.isValidPosition(nextX,nextY) && grid.isWalkable(nextX,nextY)){
                    if(!visited[nextX][nextY]){
                        Position neighbour=new Position(nextX,nextY);
                        visited[nextX][nextY]=true;
                        parentMap.put(neighbour,current);
                        queue.add(neighbour);
                    }
                }
            }
        }

        if(found){
            return reconstructPath(parentMap, start, target);
        }

        return new ArrayList<>();
    }

    private List<Position> reconstructPath(Map<Position,Position> parentMap,Position start,Position target){
        List<Position> path=new ArrayList<>();
        Position current=target;

        while (current!=null && !(current.getX()== start.getX() && current.getY()==start.getY())){
            path.add(0,current);
            current=parentMap.get(current);
        }

        return path;
    }

}
