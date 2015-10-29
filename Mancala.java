import java.io.*;
import java.util.*;


public class Mancala 
{
	
	public static int size;
	
	public static class Nsort implements Comparator<String>
	   {
		   public int compare(String A , String B)
		   {
			   return A.compareTo(B);
		   }
	   }
	public static class Ssort implements Comparator<StateNode>
	   {
		   public int compare(StateNode A , StateNode B)
		   {
			   return A.name.compareTo(B.name);
		   }
	   }
	public static class treesort implements Comparator<Node>
	   {
		   public int compare(Node A , Node B)
		   {
			   return A.name.compareTo(B.name);
		   }
	   }
	
	public static class Node
	{
		String name;
		int depth;
		int value;
		Node parent;
		PriorityQueue <Node> Childlist;
		String[] childlist;
		String Type;
		StateNode stateHere;
	}
	
	public static class StateNode
	{
		String name;
		PriorityQueue <String> bList;
	    PriorityQueue <String> aList;
	    
	    PriorityQueue <StateNode> Children;
	    HashMap<String,Integer> board;	    
		StateNode parent;
		int depth;
		int P1mancala;
		int P2mancala;
		boolean P1turn;
		boolean repeatchance;
		Node treenode;
	}
	
	
	public static StateNode root = new StateNode();
	public static Node Root = new Node();
	public static int cutoff;
	public static int plusinfinity = 99999999;
	public static int minusinfinity = -9999999;
	public static int depthcounter;
	public static Deque <String> traverseLog = new ArrayDeque<String>();
	
	
	
	public static int calculateutil(StateNode x)
	{
			//String a = x.name;			
			if(me == 1)
			{
				return x.P1mancala - x.P2mancala;
			}
			else
			{
				return x.P2mancala - x.P1mancala;
			}					
	}
	
	public static StateNode replicate(StateNode boardstate,StateNode parentstate)
	{
		boardstate.board =new HashMap<String,Integer>(); 
		boardstate.parent = parentstate;

		Ssort S = new Ssort();
		Nsort Np = new Nsort();
		Nsort Nq = new Nsort();
		boardstate.Children = new PriorityQueue <StateNode>(10, S);
		boardstate.aList = new PriorityQueue <String>(10, Nq);
		boardstate.bList = new PriorityQueue <String>(10, Np);
		boardstate.repeatchance = false;
		boardstate.P1mancala = parentstate.P1mancala;
		boardstate.P2mancala = parentstate.P2mancala;
		boardstate.P1turn = parentstate.P1turn;
		
		Iterator<String> I = parentstate.bList.iterator();
		while(I.hasNext())
		{
			String ne = I.next();
			boardstate.bList.add(ne);
		}
				
		Iterator<String> It = parentstate.aList.iterator();
		while(It.hasNext())
		{
			String xg = It.next();
			boardstate.aList.add(xg);
		}
		
		boardstate.board.putAll(parentstate.board);

		return boardstate;
		
	}

	
	public static StateNode CreateStateNode(StateNode parentstate,String startpit,int curdepth)
	{
			StateNode boardstate = new StateNode();
						
			boardstate = replicate(boardstate,parentstate);	
			boardstate.depth = curdepth;
			boardstate.name = startpit;		

			int coins = boardstate.board.get(startpit);
			boardstate.board.put(startpit,0);
			
			String lastpit = "";
			String mymancala;
			String me;
/*			System.out.println("--------" + boardstate.P1turn +"--------"+ boardstate.name);*/
			if(boardstate.P1turn==true)
			{
				String curr = "B";
				me = "B";
				mymancala = "P1mancala";
				String I[] = startpit.split("B");
				int i = Integer.parseInt(I[1])+1;
				
				int j=size+1;
				String currj = "A";
				while(coins > 0)
				{
					if(coins>0 && i<size+2)
					{
						curr = curr + i;
						lastpit = curr;
						int t = boardstate.board.get(curr);
						t++;
						boardstate.board.put(curr,t);
						coins--;
						i++;
						curr = "B";
					}
					else if(coins>0 && i==size+2)
					{
						lastpit = "P1mancala";
						boardstate.P1mancala++;
						coins--;
						i++;
					}
					else if(coins>0 && i>size+2 && j>1)
					{
						currj = currj + j;
						//System.out.println(currj);
						lastpit = currj;
						int t = boardstate.board.get(currj);
						//System.out.println(t);
						t++;
						boardstate.board.put(currj,t);
						coins--;
						j--;
						currj = "A";
					}
					else if(coins>0 && j==1)
					{
						i = 2;
						j=size+1;
					}
				}
				
			}
			else
			{
				String curr = "A";
				me = "A";
				mymancala = "P2mancala";
				String I[] = startpit.split("A");
				int i = Integer.parseInt(I[1])- 1;
				int j=2;
				String curri = "B";
				while(coins > 0)
				{
					if(coins>0 && i>1)
					{
						curr = curr + i;
						lastpit = curr;
						int t = boardstate.board.get(curr);
						t++;
						boardstate.board.put(curr, t);
						i--;
						coins--;
						curr = "A";
					}
					else if(coins>0 && i==1)
					{
						lastpit = "P2mancala";
						boardstate.P2mancala++;
						coins--;
						i--;
					}
					else if(coins>0 && i<1 && j<size+2)
					{
						curri = curri + j;
						lastpit = curri;
						int t = boardstate.board.get(curri);
						t++;
						boardstate.board.put(curri,t);
						coins--;
						j++;
						curri = "B";
					}
					else if(coins>0 && j==size+2)
					{
						i=size+1;
						j=2;
					}
					
				}
				
			}
/*			System.out.println("Lastpit ended ---"+lastpit+"\n\n");*/
			if(lastpit == mymancala)//if last coin was in player's mancala
			{
				//set repeatchance to true
				boardstate.repeatchance = true;
			}
			//check if the last coin ended in my side
			else if(lastpit.substring(0,1).equals(me) && boardstate.board.get(lastpit)==1)//if last coin was in player's empty pit
			{
				//take all the coins from across the board of the opponent and add it your mancala
				String[] get = lastpit.split(me);
				String curr;
				int i = Integer.parseInt(get[1]);
				if(me == "B")
				{
					curr = "A";
				}
				else
				{
					curr = "B";
				}
				curr = curr+i;
				
				int t = boardstate.board.get(curr);
				/*if(t>0)
				{*/
					t += boardstate.board.get(lastpit);
					boardstate.board.put(lastpit,0);
					boardstate.board.put(curr,0);
					if(me == "B")
					{
						boardstate.P1mancala += t;
					}
					else
					{
						boardstate.P2mancala += t;
					}
					
				/*}*/
				
			}
		boardstate = checkendgamestate(boardstate);//Check for the end game state
		
		return boardstate;	
	}
	
	public static Node replicateTreenode(Node curtree,Node par)
	{
		treesort ka = new treesort();
	    curtree.Childlist = new PriorityQueue <Node>(10, ka);							
		
		return curtree;
	}
	
	public static String sendInfinity(int x)
	{
		String S = "";
		if(x == minusinfinity)
		{
			S = "-Infinity";
		}
		else if(x == plusinfinity)
		{
			S = "Infinity";
		}
		else
		{
			S = Integer.toString(x);
		}
		return S;
	}
	
	public static void evaluate(StateNode parentstate,String startpit,Node par,int currdepth)
	{
		
		/*if(parentstate.board.get(startpit) == 0)//if the current pit has no coins then do not create a new state node
		{
			return;
		}*/
		
		StateNode newState = CreateStateNode(parentstate,startpit,currdepth);//create a new state node by making a move
		//System.out.println(newState.name);
		newState.parent = parentstate;//point it to its parent
				
		Node curtree = new Node();
		curtree = replicateTreenode(curtree,par);
		curtree.name = startpit;
		curtree.parent = par;
	
		par.Childlist.add(curtree);

		newState.treenode = curtree;
		curtree.stateHere = newState;
		curtree.depth = currdepth;
		if(currdepth%2 == 0 && newState.repeatchance==false)
		{
			curtree.Type = "MAX";
			curtree.value = minusinfinity;
		}
		else if(currdepth%2 != 0 && newState.repeatchance==false)
		{
			curtree.Type = "MIN";
			curtree.value = plusinfinity;
		}
		else if(currdepth%2 ==0 && newState.repeatchance==true)
		{
			curtree.Type = "MIN";
			curtree.value = plusinfinity;
		}
		else if(currdepth%2 != 0 && newState.repeatchance==true)
		{
			curtree.Type = "MAX";
			curtree.value = minusinfinity;
		}	
		
		
		if(newState.repeatchance == true)
		{
			Iterator<String> X ;
			if(newState.P1turn == true)
			{
				X = newState.bList.iterator();
			}
			else
			{
				X = newState.aList.iterator();
			}
			while(X.hasNext())
			{
				String zz = X.next();
				if(newState.board.get(zz)>0)
				{
				evaluate(newState,zz,newState.treenode,currdepth);
				}
			}
			//newState.repeatchance = false;
/*			System.out.println(curtree.name+"------------->>"+curtree.value+"--------------Type:"+curtree.Type);
*/		}
		else
		{
			int util = calculateutil(newState);
			if(currdepth == cutoff)
			{
				curtree.value = util;
				
			}
		
			if(newState.P1turn==true)
			{
				newState.P1turn = false;
			}
			else
			{
				newState.P1turn = true;
			}	
/*			System.out.println(curtree.name+"------------->>"+curtree.value+"--------------Type:"+curtree.Type);
*/			MainQ.addLast(newState);
						
		}	
		
	
	}
	
	public static int Max_Value(Node mincurr)
	{
		int v = minusinfinity;
		
		if(mincurr.depth == cutoff && mincurr.stateHere.repeatchance==false)//if terminal node and has no children
		{
			return mincurr.value;
		}
		else
		{
			Iterator <Node> maxpointer = mincurr.Childlist.iterator();
			
			while(maxpointer.hasNext())
			{
				
				Node maxcurr = maxpointer.next();
				int now;
				
							
				/*if(maxcurr.depth==cutoff && maxcurr.stateHere.repeatchance==true)
				{
					maxcurr.value = calculateutil(maxcurr.stateHere);
				}*/
				
				String traverse = maxcurr.name+","+maxcurr.depth+","+sendInfinity(maxcurr.value);
				traverseLog.push(traverse);
				
				//again if the node is an intermediate node check whether MAX or MIN
				if(maxcurr.Type.equals("MAX"))
				{
					now = Max_Value(maxcurr);
				}
				else
				{
					now = Min_Value(maxcurr);
				}					
				if(now > v)
				{
					v = now;
				}

				if(v>mincurr.value)
				{
					mincurr.value = v;
					
				}
				traverse = mincurr.name+","+mincurr.depth+","+sendInfinity(mincurr.value);
				traverseLog.push(traverse);
	
			}
			//if the current node is a endgame state with repeat chance
			if(mincurr.stateHere.repeatchance == true && mincurr.Childlist.size()<1)
			{
				mincurr.value = calculateutil(mincurr.stateHere);
				v = mincurr.value;
				String traverse = mincurr.name+","+mincurr.depth+","+sendInfinity(mincurr.value);
				traverseLog.push(traverse);
				
			}
			
		}
		
		return v;
	}
	
	public static int Min_Value(Node curr)
	{
		int v=plusinfinity;
		if(curr.depth == cutoff && curr.stateHere.repeatchance==false)
		{
			return curr.value;
		}
		else
		{
			v = plusinfinity;
			Iterator <Node> minpointer = curr.Childlist.iterator();
			while(minpointer.hasNext())
			{
			
				Node mincurr = minpointer.next();
				
							
				/*if(mincurr.depth == cutoff && mincurr.stateHere.repeatchance==true)
				{
					mincurr.value = calculateutil(mincurr.stateHere);
				}*/
				
				String traverse = mincurr.name+","+mincurr.depth+","+sendInfinity(mincurr.value);
				traverseLog.push(traverse);
				
				
				int now;
				if(mincurr.Type.equals("MAX"))
				{
					now = Max_Value(mincurr);
					if(now > mincurr.value)
					{
						mincurr.value = now;
					}
					
				}
				else
				{
					now = Min_Value(mincurr);
					if(now < mincurr.value)
					{
						mincurr.value = now;
					}
					
				}
				if(now < v)
				{
					v = now;
				}

				if(v < curr.value)
				{
					curr.value = v;
				}				
				traverse = curr.name+","+curr.depth+","+sendInfinity(curr.value);
				traverseLog.push(traverse);
			}
			
			if(curr.stateHere.repeatchance == true && curr.Childlist.size()<1)
			{
				curr.value = calculateutil(curr.stateHere);
				v = curr.value;
				String traverse = curr.name+","+curr.depth+","+sendInfinity(curr.value);
				traverseLog.push(traverse);
			}
			
		}
		
		return v;
	}
	
	public static int Max_V(Node current,int alpha,int beta)
	{
		int v=minusinfinity;
		if(current.depth==cutoff && current.stateHere.repeatchance==false)
		{
			return current.value;
		}

		Iterator<Node> pointer = current.Childlist.iterator();
		while(pointer.hasNext())
		{
			
			Node child = pointer.next();
			
			/*if(child.depth==cutoff && child.stateHere.repeatchance==true)
			{
				child.value = calculateutil(child.stateHere);
			}*/
			
			String x = child.name+","+child.depth+","+sendInfinity(child.value)+","+sendInfinity(alpha)+","+sendInfinity(beta);
			traverseLog.push(x);
			
			if(child.Type.equals("MIN"))
			{
				v = Math.max(v, Min_V(child,alpha,beta));
				if(v < child.value)
				{
					child.value = v;
				}
				
			}
			else
			{
				v = Math.max(v, Max_V(child,alpha,beta));
				/*if(v > alpha)
				{
					//alpha = v;
					//current.value = v;
				}*/
				
				if(v > child.value)
				{
					child.value = v;
				}
			}
			
			if(child.stateHere.repeatchance == true && child.Childlist.size()<1)
			{
				v = calculateutil(child.stateHere);
				child.value = v;
				
				x = child.name+","+child.depth+","+sendInfinity(child.value)+","+sendInfinity(alpha)+","+sendInfinity(beta);
				traverseLog.push(x);
				
			}
			
			if(v >= beta)
			{
				if(current.Type.equals("MIN"))
				{
					if(v < current.value)
					{
						current.value = v;
					}
				}
				if(current.Type.equals("MAX"))
				{
					if(v > current.value)
					{
						current.value = v;
					}
				}
				x = current.name+","+current.depth+","+sendInfinity(current.value)+","+sendInfinity(alpha)+","+sendInfinity(beta);
				traverseLog.push(x);
				return v;
			}
			
			alpha = Math.max(alpha,v);
			
			/*if(child.stateHere.repeatchance== true && child.depth<cutoff )
			{		
				System.out.println(child.name+","+child.depth+","+sendInfinity(child.value)+","+sendInfinity(alpha)+","+sendInfinity(beta));
			}*/
			if(current.Type.equals("MIN"))
			{
				if(v < current.value)
				{
					current.value = v;
				}
			}
			if(current.Type.equals("MAX"))
			{
				if(v > current.value)
				{
					current.value = v;
				}
			}
			
				x = current.name+","+current.depth+","+sendInfinity(current.value)+","+sendInfinity(alpha)+","+sendInfinity(beta);
				traverseLog.push(x);
			
		}
		
		return v;		
	}
	
	public static int Min_V(Node current,int alpha,int beta)
	{
		int v = plusinfinity;
		
		if(current.depth==cutoff && current.stateHere.repeatchance==false)
		{
			return current.value;
		}
		
		Iterator <Node> pointer = current.Childlist.iterator();
		while(pointer.hasNext())
		{
			Node child = pointer.next();
			
			/*if(child.depth==cutoff && child.stateHere.repeatchance==true)
			{
				child.value = calculateutil(child.stateHere);
			}*/			

			String x = child.name+","+child.depth+","+sendInfinity(child.value)+","+sendInfinity(alpha)+","+sendInfinity(beta);
			traverseLog.push(x);
			
			if(child.Type.equals("MIN"))
			{
				v = Math.min(v, Min_V(child,alpha,beta));
				/*if(v < beta)
				{
					//beta = v;
					//current.value = v;
				}*/
				if(v < child.value)
				{
					child.value = v;
				}
			}
			else
			{
				v = Math.min(v, Max_V(child,alpha,beta));
				if(v > child.value)
				{
					child.value = v;
				}
			}
			
			if(child.stateHere.repeatchance == true && child.Childlist.size()<1)
			{
				v = calculateutil(child.stateHere);
				child.value = v;
				
				x = child.name+","+child.depth+","+sendInfinity(child.value)+","+sendInfinity(alpha)+","+sendInfinity(beta);
				traverseLog.push(x);
				
			}
					
			if(v <= alpha)
			{
				if(current.Type.equals("MIN"))
				{
					if(v < current.value)
					{
						current.value = v;
					}
				}
				if(current.Type.equals("MAX"))
				{
					if(v > current.value)
					{
						current.value = v;
					}
				}
				x = current.name+","+current.depth+","+sendInfinity(current.value)+","+sendInfinity(alpha)+","+sendInfinity(beta);
				traverseLog.push(x);
				return v;
			}
			
						
			beta = Math.min(beta, v);
			
			
			/*if(child.stateHere.repeatchance==true && child.depth < cutoff)
			{
			System.out.println(child.name+","+child.depth+","+sendInfinity(child.value)+","+sendInfinity(alpha)+","+sendInfinity(beta));
			}*/
			
			if(current.Type.equals("MIN"))
			{
				if(v < current.value)
				{
					current.value = v;
				}
			}
			if(current.Type.equals("MAX"))
			{
				if(v > current.value)
				{
					current.value = v;
				}
			}
			
			
			x = current.name+","+current.depth+","+sendInfinity(current.value)+","+sendInfinity(alpha)+","+sendInfinity(beta);
			traverseLog.push(x);
			
			
		}
		return v;		
	}
	
	public static StateNode checkendgamestate(StateNode chosen)
	{
		int Acount = 0;		
		Iterator<String> iter = chosen.aList.iterator();
		while(iter.hasNext())
		{
			String x = iter.next();
			if(chosen.board.get(x)==0)
			{
				Acount++;
			}
		}
		
		int Bcount = 0;
		Iterator<String> itr = chosen.bList.iterator();
		while(itr.hasNext())
		{
			String x = itr.next();
			if(chosen.board.get(x)==0)
			{
				Bcount++;
			}
		}
		
		if(Acount==size && Bcount==size)
		{
			return chosen;			
		}
		if(Acount==size)
		{
			int t=0;
			Iterator<String> pointer = chosen.bList.iterator();
			while(pointer.hasNext())
			{
				String x = pointer.next();
				t = t + chosen.board.get(x);
				chosen.board.put(x,0);
			}
			chosen.P1mancala += t;
			return chosen;
		}
		if(Bcount==size)
		{
			int t=0;
			Iterator<String> pointer = chosen.aList.iterator();
			while(pointer.hasNext())
			{
				String x = pointer.next();
				t = t + chosen.board.get(x);
				chosen.board.put(x,0);
			}
			chosen.P2mancala += t;
			return chosen;
			
		}
		else		
			return chosen;
	}
	
	public static Node Alpha_Beta(Node chosenone)
	{
		int alpha = minusinfinity,beta=plusinfinity,v;
		
		/*String x = chosenone.name+","+chosenone.depth+","+sendInfinity(chosenone.value)+","+sendInfinity(alpha)+","+sendInfinity(beta);
		traverseLog.push(x);*/
		v = Max_V(chosenone,alpha,beta);
		
		Node chosen = displayAlpha(Root,v);
		
		//Node FinalChosen = checkendgamestate(chosen);		
		
		displayResult(chosen);
					
		return chosenone;
	}
	
	public static Node displayAlpha(Node N, int x)
	{
		Node sendThis = null;
		if(N.depth==cutoff && N.stateHere.repeatchance==false && N.value == x)
		{
			return N;
		}
		Iterator <Node> it = N.Childlist.iterator();
		while(it.hasNext())
		{
			Node ex = it.next();
			if(ex.value == x && ex.stateHere.repeatchance==false)
			{
				sendThis = ex;
				break;
			}
			else if(ex.value == x && ex.stateHere.repeatchance==true)
			{
				sendThis = displayAlpha(ex,x);
				break;
			}
		}
		
		if(N.stateHere.repeatchance == true && N.Childlist.size()<1)
		{
			sendThis = N;			
		}
		
		return sendThis;
				
	}
	
	public static Node minimax(Node x)
	{
		//return max of the min-value of its children
		Node sendthis = null;
		int maxval = minusinfinity;		
		
			Iterator <Node> pointer = x.Childlist.iterator();
			while(pointer.hasNext())
			{
			
				Node curr = pointer.next();
				int retvalue;
				//send the parent value to the child if it has a max/min value compared to what parent already has
								
				String traverse = curr.name+","+curr.depth+","+sendInfinity(curr.value);
				traverseLog.push(traverse);
				
				if(curr.Type.equals("MAX"))
				{
					retvalue = Max_Value(curr);
					if(retvalue > curr.value)
					{
						curr.value = retvalue;
					}
			
				}
				else
				{
					retvalue = Min_Value(curr);
					if(retvalue < curr.value)
					{
						curr.value = retvalue;
					}
					
				}			
			
				if(retvalue > maxval)
				{
					maxval = retvalue;
					sendthis = curr;
				}
				if(x.value < maxval)
				{
					x.value = maxval;
				}
				traverse = x.name + ","+x.depth + ","+ sendInfinity(x.value);
				traverseLog.push(traverse);
				
			}
			
			
		return sendthis;
		
	}
	
	
	public static LinkedList <StateNode> MainQ = new LinkedList<StateNode>();	
	public static int me;
	
	public static Node displayState(Node thismove)
	{
			int v = minusinfinity;
			Iterator<Node>iter = thismove.Childlist.iterator();
			while(iter.hasNext())
			{
				Node x = iter.next();
				if(x.value > v)
				{
					v = x.value;
					if(x.stateHere.repeatchance == true)
						{
						thismove = displayState(x);
						}
					else
						{
						thismove = x;
						}					
				}
			}		
		return thismove;	
						
	}
	
	public static void displayStack()
	{
		Deque<String> Tmp = new ArrayDeque<String>();
		String outputfileName = "traverse_log.txt";
		while(!traverseLog.isEmpty())
		{
			Tmp.push(traverseLog.pop());
		}
		try
		{
			FileWriter in = new FileWriter(outputfileName);
			BufferedWriter out = new BufferedWriter(in);
		
			while(!Tmp.isEmpty())
			{
				//System.out.println(Tmp.pop());
				out.write(Tmp.pop());
				out.newLine();
			}
			out.close();	
		}
		catch(IOException ex) 
		{
            System.out.println("Error reading file '" + outputfileName + "'");                   
        }
	}
	
	public static void displayResult(Node chosenone)
	{
		String aa = "";
		String bb = "";
		String outputfileName = "next_state.txt";
		
		try
		{
			FileWriter fileWriter = new FileWriter(outputfileName);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		//BufferedWriter  = new BufferedWriter(fileWriter);		
		
		Iterator <String>curAChild = chosenone.stateHere.aList.iterator();
		while(curAChild.hasNext())
		{
			String c = curAChild.next();
			aa = aa+chosenone.stateHere.board.get(c)+" ";							
		}
		Iterator <String>curBChild = chosenone.stateHere.bList.iterator();
		while(curBChild.hasNext())
		{
			String c = curBChild.next();
			bb = bb +chosenone.stateHere.board.get(c)+" ";							
		}
		bufferedWriter.write(aa);
		bufferedWriter.newLine();
		
		bufferedWriter.write(bb);
		bufferedWriter.newLine();
		
		String x = Integer.toString(chosenone.stateHere.P2mancala);
		bufferedWriter.write(x);
		bufferedWriter.newLine();
		
		x = Integer.toString(chosenone.stateHere.P1mancala);
		bufferedWriter.write(x);
		bufferedWriter.newLine();
		
		bufferedWriter.close();				
		/*System.out.println(aa);
		System.out.println(bb);
		System.out.println(chosenone.stateHere.P2mancala);
		System.out.println(chosenone.stateHere.P1mancala);*/
		}
		catch(IOException ex) 
		{
            System.out.println("Error reading file '" + outputfileName + "'");                   
        }
						
	}
	
	
	
	public static void MinMax(int algoType)
	{
		Node chosenone = null;
		chosenone = Root;
		
		String traverse = "Node,Depth,Value";
		traverseLog.push(traverse);
		
		traverse = Root.name+","+Root.depth+","+sendInfinity(Root.value);
		traverseLog.push(traverse);
		
		chosenone = minimax(chosenone);
		if(algoType > 1)
		{
		displayStack();
		}
		if(chosenone.stateHere.repeatchance == true)
		{
		chosenone = displayState(chosenone);
		}
		
		//Node FinalChosen = checkendgamestate(chosenone);
		
		displayResult(chosenone);
				
	}
	
	public static void AlphaBeta()
	{
		Node chosenone = null;
		chosenone = Root;
		
		String traverse = "Node,Depth,Value,Alpha,Beta";
		traverseLog.push(traverse);
		
		traverse = Root.name+","+Root.depth+","+sendInfinity(Root.value)+","+sendInfinity(minusinfinity)+","+sendInfinity(plusinfinity);
		traverseLog.push(traverse);
		
		chosenone = Alpha_Beta(chosenone);
		
		displayStack();
		
		//System.out.println(chosenone.value);
		
		//displayStack();
		if(chosenone.stateHere.repeatchance == true)
		{
		chosenone = displayState(chosenone);
		}
		//displayResult(chosenone);
		
		
		
	}
	
	 public static void main(String [] args) {

	        if(!args[0].equals("-i"))
			{
				System.out.println(" Not a valid command. The first argument should be '-i' followed by the inputFile name");
				System.exit(0);
			}
			String fileName = args[1]; // file name from the 1st command line argument
						
		
	        try {
	                FileReader fileReader = new FileReader(fileName);
	                LineNumberReader linereader = new LineNumberReader(fileReader);
					depthcounter = 0;
					
					int Algo = Integer.parseInt(linereader.readLine());//Task
					

					me = Integer.parseInt(linereader.readLine());//My player
					
					cutoff = Integer.parseInt(linereader.readLine());//cutoff
					
					if(Algo==1)//if the task is Greedy then set cutoff to 1
					{
						cutoff = 1;
					}
					
					String player2 = linereader.readLine();//player 2 status
					String player1 = linereader.readLine();//player 1 status
					
					int player2mancala = Integer.parseInt(linereader.readLine());//player 2 mancala count
					int player1mancala = Integer.parseInt(linereader.readLine());// player 1 mancala count
					
					String[] P2 = player2.split(" ");
					String[] P1 = player1.split(" ");
					size = P2.length;
					int[] playr2 = new int[size];
					int[] playr1 = new int[size];
					
					for(int i =P2.length-1;i>=0;i--)
					{
						playr2[i] = Integer.parseInt(P2[i]);
					}
					for(int i =0;i<P1.length;i++)
					{
						playr1[i] = Integer.parseInt(P1[i]);
					}
					
					root.parent = null;
					root.name = "root";
					root.board =new HashMap<String,Integer>(); 
					Ssort S = new Ssort();
					Nsort Np = new Nsort();
					root.Children = new PriorityQueue <StateNode>(10, S);
					
					root.aList = new PriorityQueue <String>(10, Np);
					root.bList = new PriorityQueue <String>(10, Np);
					
					root.repeatchance = false;
					if(me == 1)
					{
						root.P1mancala = player1mancala;
						root.P2mancala = player2mancala;
						for(int i=0;i<size;i++)
						{
							String B = "B";
							B = B + (i+2);
							String A = "A";
							A = A + (i+2);
							root.bList.add(B);
							root.aList.add(A);
							root.board.put(B,playr1[i]);
							root.board.put(A,playr2[i]);
							
						}
						root.depth = 0;
						root.P1turn = true;
						
					}
					else
					{
						root.P1mancala = player1mancala;
						root.P2mancala = player2mancala;
						for(int i=0;i<size;i++)
						{
							String B = "B";
							B = B + (i+2);
							String A = "A";
							A = A + (i+2);
							root.bList.add(B);
							root.aList.add(A);
							root.board.put(B,playr1[i]);
							root.board.put(A,playr2[i]);
							
						}
						root.depth = 0;	
						root.P1turn = false;
						
					}
					
					treesort ka = new treesort();
				    Root.Childlist = new PriorityQueue <Node>(10, ka);					
					Root.parent = null;
				    Root.value = minusinfinity;
				    Root.depth = 0;
				    Root.name = "root";
				    Root.Type = "MAX";
				    Root.stateHere = root;
				    
				   // System.out.println(Root.name + "value :" + Root.value + "Type :" + Root.Type);
				    
				    root.treenode = Root;
				    root.repeatchance = false;
				    MainQ.addLast(root);
				    /*
				    Iterator<Map.Entry<String, Integer>> iterator = root.board.entrySet().iterator() ;
			        while(iterator.hasNext())
			        {
			            Map.Entry<String, Integer> cc = iterator.next();
			            System.out.println(cc.getKey()+ "Value :" + cc.getValue());					
			        }
					*/
				    
					while(!MainQ.isEmpty() && depthcounter <= cutoff)
					{
					
						StateNode curr = null;
						curr = MainQ.poll();
						if(curr.name == "root")
						{
							depthcounter = 1;
						}
						else
						{
							depthcounter = curr.depth + 1;
							if(depthcounter>cutoff)
							{
								break;
							}
						}
						
						
						
						Iterator<String> it;
/*						System.out.println("----------");
						System.out.println("Current Node :" + curr.name);*/			
						if(curr.P1turn == true)
						{
							it = curr.bList.iterator();
						}
						else
						{
							it = curr.aList.iterator();	
						}
						
						while(it.hasNext())
						{
							String child = it.next();
							if(curr.board.get(child)>0)
							{
							evaluate(curr,child,curr.treenode,depthcounter);
							}
							
						}	

						
					}
									
					
					if(Algo == 1)//Greedy Algorithm
					{
						cutoff = 1;
						MinMax(1);
					}
					
					if(Algo == 2)//Min-Max Algorithm
					{
						MinMax(2);					
					}		
					
					if(Algo == 3)//Alpha-Beta Pruning Algorithm
					{
						AlphaBeta();					
					}
					
					if(Algo == 4)//Competition
					{
						AlphaBeta();					
					}
					
	              linereader.close();  
	          }
				
			catch(FileNotFoundException ex) 
			{
	            System.out.println("Unrecognised file '" + fileName + "'");                
	        }
	        catch(IOException ex) 
			{
	            System.out.println("Error reading file '" + fileName + "'");                   
	        }		

	}	

}
