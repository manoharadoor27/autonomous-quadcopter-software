close;clear;clc;
%mapr=imread('traquad.jpg');
mapr=[3 0 2 0 0 0 0 0 0 0;
        0 2 2 0 0 1 0 0 2 3;
        3 0 3 2 0 0 0 0 0 2;
        0 0 2 3 0 0 0 0 0 0;
        0 0 0 0 2 0 0 0 0 0;
        0 0 0 0 0 0 2 0 0 0;
        0 0 0 0 0 0 0 0 0 2;];
%Input
noe=0;
%Map quadcopter is a copy that is made a global variable
global map_quadcopter
global mapout
map_quadcopter=mapr;
leny=length(mapr(1,:));
lenx=length(mapr(:,1));

mapo=zeros(lenx,leny);

for i=1:lenx
    for j=1:leny
        if(mapr(i,j)>1)
            mapo(i,j)=mapo(i,j)+1;
        end
    end
end

%The outer boundary is padded with zeros.

m=mapo; %Original map. Generating a copy for safety
m=[zeros(1,length(m(1,:))); m; zeros(1,length(m(1,:)))];
m=[zeros(length(m(:,1)),1) m zeros(length(m(:,1)),1)];
%leny=length(m(1,:))
%lenx=length(m(:,1))

node=[];
sad=[]; %Sources and Destinations
required_node_matrix=[];
for i=2:(lenx-1)
    for j=2:(leny-1)
        number=m((i-1),j)+m((i+1),j)+m(i,(j+1))+m(i,(j-1))+m((i-1),(j-1))+m((i-1),(j+1))+m((i+1),(j-1))+m((i+1),(j+1));
        if(m(i,j)==1)
            if(number>2)
                node=[node; j i];
                required_node_matrix=[required_node_matrix; j i];
            end
            if(number==1)
                sad=[sad; j i];
                required_node_matrix=[required_node_matrix; j i];
            end
        end
    end
end
%node
if(isempty(node)==0)
noe=length(node(:,1));  %number of nodes (elements) listed
nodeout=zeros(lenx,leny);
noe_size=size(noe(1,:));

for k=1:noe
    i=node(k,2);
    j=node(k,1);
    nodeout(i,j)=1;
end
end

sade=length(sad(:,1));  %number of Sources and Destinations (elements) listed
sadout=zeros(lenx,leny);
for k=1:sade
    i=sad(k,2);
    j=sad(k,1);
    sadout(i,j)=1;
end

map_send=mapr;
[P Q]=size(required_node_matrix);

global part_matrix;
global mapreproduced
global roadmap
global weight_return
global node_initial_x
global node_initial_y
global node_final_x
global node_final_y
global adjacency_matrix

part_matrix=map_send;

adjacency_matrix=zeros(P,P);
%Permutations and combinations between two nodes
for i=1:P
    for j=1:P
            node_initial_x=required_node_matrix(i,1);
            node_initial_y=required_node_matrix(i,2);
            node_final_x=required_node_matrix(j,1);
            node_final_y=required_node_matrix(j,2);
            
                  weight_return=traquad_probability(node_initial_x,node_initial_y,node_final_x,node_final_y)
                  adjacency_matrix(i,j)=adjacency_matrix(i,j)+abs(weight_return);
            
    end
end
traquad_heuristic(adjacency_matrix);
map=m;
mapout=nodeout+sadout;