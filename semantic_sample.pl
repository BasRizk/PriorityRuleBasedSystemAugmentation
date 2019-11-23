:-use_module(library(chr)).

:-chr_constraint start/0,
	conflictdone/0,fire/0,
	history/1,
	match/3,a/0, a/1 ,b/0, b/1, c/0, c/1, d/0, d/1,
	id/1,
	getTrueOrFalse/0.	

id(I),a <=> a(I), I1 is I+1, id(I1).
id(I),a <=> a(I), I1 is I+1,id(I1).
id(I),b <=> b(I), I1 is I+1,id(I1).
id(I),c <=> c(I), I1 is I+1,id(I1).
	
	
	
r1 @  start,a(ID) ==> match(r1,[ID],3).
r2 @  start,a(ID) ==> match(r2,[ID],1).
r3 @  start,a(ID) ==> match(r3,[ID],1).

start <=> conflictdone.

history(L) \ match(R,IDs,_) <=> sort(IDs, IDsSorted), member((R,IDsSorted),L) | true.


conflictdone, match(_,_,O1) \ match(_,_,O2) <=> O1<O2 | true.
conflictdone, match(_,_,O1) \ match(_,_,O2) <=> O1=O2, getTrueOrFalse | true.
getTrueOrFalse <=> random(X), X > 0.5.
conflictdone <=> fire.
   
r4 @  a(ID) \ history(L),fire,match(r1,IDs,_) <=> member(ID,IDs) | print('fired r1'),nl,b,history([(r1,[ID])|L]),start.
r5 @  fire,a(ID),match(r2,IDs,_),history(L) <=> member(ID,IDs) |print('fired r2'),nl,c,history([(r2,[ID])|L]),start.
r6 @  a(ID)\fire,match(r3,IDs,_),history(L) <=> member(ID,IDs) |print('fired r3'),nl,d,history([(r3,[ID])|L]),start.

fire<=>true.