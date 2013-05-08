package scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;

import scheduler.Activation;

public class GraphTransitivityRemover {

    public ListenableDirectedGraph<Activation, DefaultEdge>
            graph_to_single_source_longest_path(final ListenableDirectedGraph<Activation, DefaultEdge> graph){

        ListenableDirectedGraph<Activation, DefaultEdge> newGraph =
                new ListenableDirectedGraph<Activation, DefaultEdge>(DefaultEdge.class);
		
        // the vertices to be visited.
        List<Activation> current_vertex_list = new ArrayList<Activation>();
        // all the vertices visited so far
        List<Activation> all_visited_vertex_list = new ArrayList<Activation>();
        // from root to the node, how many nodes connecting to it
        Map<Activation, List<Activation>> parentOf_Map = new HashMap<Activation, List<Activation>>();
		
        for(Activation act : graph.vertexSet()){
            newGraph.addVertex(act);
            parentOf_Map.put(act, new ArrayList<Activation>());
			
            //TODO: check it out
            if(graph.inDegreeOf(act) == 0){
                current_vertex_list.add(act);
                all_visited_vertex_list.add(act);
            }
        }// end of for loop

        if(current_vertex_list.isEmpty()){
            for(Activation act : graph.vertexSet()){

                current_vertex_list.add(act);
                all_visited_vertex_list.add(act);
            }// end of for loop
        }
		
        //TODO: make sure this is the only one
        Activation root_vertex = current_vertex_list.get(0);
        while(!current_vertex_list.isEmpty()){
            Activation current_activation = current_vertex_list.remove(0);
		
            Set<DefaultEdge> _current_outgoing_set = graph.outgoingEdgesOf(current_activation);
			
            for(DefaultEdge _ed : _current_outgoing_set){
                Activation src_act = current_activation;
                Activation dest_act = graph.getEdgeTarget(_ed);
				
                // if no edge in original graph then 
                // don't consider such possibility
                if(!graph.containsEdge(src_act, dest_act)){
                    continue;
                }
                // reflection and symmetric
                if(src_act == dest_act || newGraph.containsEdge(dest_act, src_act)){
                    continue;
                }
				
                // if dest_activation has not already been visited add it to visit list
                if(!all_visited_vertex_list.contains(dest_act)){
					
                    all_visited_vertex_list.add(dest_act);
                    current_vertex_list.add(dest_act);
                }
				
                // if there is already some path is the newGraph to this graph
                if(doesPathExist(root_vertex, dest_act, newGraph)){
                    List<Activation> parentList = parentOf_Map.get(dest_act);
					
                    for(int i=0 ; i<parentList.size() ; ++i){
                        Activation parentAct = parentList.get(i);

                        if(doesPathExist(parentAct, src_act, newGraph)){
                            parentList.remove(parentAct);
                            newGraph.removeEdge(parentAct, dest_act);
                        }
                    }
                }
								
                newGraph.addEdge(src_act, dest_act);
                parentOf_Map.get(dest_act).add(src_act);
            }// end of for loop
        }
        return newGraph;
    }
	
    public boolean doesPathExist(final Activation src, final Activation dest, final ListenableDirectedGraph<Activation, DefaultEdge> graph){
		
        List<Activation> _current_list = new ArrayList<Activation>();
        List<Activation> _all_visited_list = new ArrayList<Activation>();
		
        _current_list.add(src);
        _all_visited_list.add(src);		
		
        while(!_current_list.isEmpty()){
            Activation current = _current_list.remove(0);
            Set<DefaultEdge> _current_set = graph.outgoingEdgesOf(current);
			
            for(DefaultEdge edge : _current_set){
                Activation dest_act = graph.getEdgeTarget(edge);
				
                if(!_all_visited_list.contains(dest_act)){
                    if(dest_act == dest)	return true;
					
                    _current_list.add(dest_act);					
                    _all_visited_list.add(dest_act);
                }
            }
        }
		
        return false;
    }
}
