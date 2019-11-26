:- use_module(library(chr)).
:- chr_constraint b/0, a/1, a/0, c/1, d/0, b/1, c/0, d/1, 
	start/0, conflictdone/0, fire/0,
	history/1,
	match/3,
	id/1,
	getTrueOrFalse/0.

id(I), a <=> a(I), I1 is I+1, id(I1).
id(I), b <=> b(I), I1 is I+1, id(I1).
id(I), c <=> c(I), I1 is I+1, id(I1).
id(I), d <=> d(I), I1 is I+1, id(I1).

r1 @ start, a(ID1),b(ID2) ==> match(r1, [ID1,ID2],1).
r2 @ start, a(ID1) ==> match(r2, [ID1],2).
r3 @ start, a(ID1) ==> match(r3, [ID1],2).

start <=> conflictdone.

history(L) \ match(R,IDs,_) <=> sort(IDs, IDsSorted), member((R,IDsSorted),L) | true.


conflictdone, match(_,_,O1) \ match(_,_,O2) <=> O1<O2 | true.
conflictdone, match(_,_,O1) \ match(_,_,O2) <=> O1=O2, getTrueOrFalse | true.
getTrueOrFalse <=> random(X), X > 0.5.


conflictdone <=> fire.

r1 @ a(ID1),b(ID2), history(L), fire, match(r1, IDs, _) <=> member(ID1, IDs),member(ID2, IDs) | print("fired r1"), nl, c, history([(r1, [ID1,ID2])|L]), start.
r2 @ a(ID1), history(L), fire, match(r2, IDs, _) <=> member(ID1, IDs) | print("fired r2"), nl, d, history([(r2, [ID1])|L]), start.
r3 @ a(ID1), history(L), fire, match(r3, IDs, _) <=> member(ID1, IDs) | print("fired r3"), nl, c, history([(r3, [ID1])|L]), start.

fire<=>true.