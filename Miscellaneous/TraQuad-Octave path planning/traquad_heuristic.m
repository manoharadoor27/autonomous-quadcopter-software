function shortest(adjacency_matrix)
virtual_infinity=10000;
x=virtual_infinity;
initial_node_routes=adjacency_matrix(1,:);
final_node_routes=adjacency_matrix(length(adjacency_matrix(1,:)),:);

length_matrix=length(initial_node_routes);

state=1000;
city=100;
local_area=10;

%condition=input('Please enter a number: 1.State 2.City 3.Local area ');
condition=1
if condition==1
    iteration_value=state;
end

if condition==2
    iteration_value=city;
end

if condition==3
    iteration_value=local_area;
end

%Three is being taken as optimum value. This can extend upto 'n' number of nodes.

node_routes=initial_node_routes;

for  i=1:(length_matrix-1)
    for i=1:(length_matrix-1)
        if node_routes(i)>node_routes(i+1)
            %swap
            temporary=node_routes(i);
            node_routes(i)=node_routes(i+1);
            node_routes(i+1)=temporary;
        end
    end
end

initial=node_routes; %Sorted matrix needed.

if length_matrix>0
min_initial0=initial(1); %Unneeded minimum 0.
end

if length_matrix>1
min_initial1=initial(2);
end

if length_matrix>2
min_initial2=initial(3);
end

if length_matrix>3
min_initial3=initial(4);
end

node_routes=final_node_routes;

for  i=1:(length_matrix-1)
    for i=1:(length_matrix-1)
        if node_routes(i)>node_routes(i+1)
            %swap
            temporary=node_routes(i);
            node_routes(i)=node_routes(i+1);
            node_routes(i+1)=temporary;
        end
    end
end

final=node_routes; %Sorted matrix needed.

if length_matrix>0
min_final0=final(1); %Unneeded minimum 0.
end

if length_matrix>1
min_final1=final(2);
end

if length_matrix>2
min_final2=final(3);
end

if length_matrix>3
min_final3=final(4);
end

initial_sum1=0;
initial_sum2=0;
initial_sum3=0;

final_sum1=0;
final_sum2=0;
final_sum3=0;

if(length_matrix>1)

path_temporary=[];
path_pos=[];
i=1;
j=2;
k=2;
pos=2;
count=1;

%PRIMARY MINIMUM FROM THE INITIAL

while (j<=length_matrix & (count<iteration_value))% & till path_pos==node_final
    temporary_matrix=adjacency_matrix(i,k:length_matrix);
    minimum=min(temporary_matrix);
   
    for l=(k):(length_matrix)
        if (minimum==adjacency_matrix(i,l))
            pos=l;
            path_pos=[path_pos; i pos];
            path_temporary=[path_temporary adjacency_matrix(i,l)];
        end
    end
    i=pos;
    k=i+1;
    j=k;
    count=count+1; %So as to eliminate infinite looping in case that really happens.
end
sum_initial=sum(path_temporary);
end

if(length_matrix>2)

%SECOND MINIMUM FROM THE INITIAL
i=1;
j=2;
k=2;
pos=2;
count=1;
path_temporary_minimum_initial2=[];

path_pos_minimum_initial2=[];

temporary_matrix=adjacency_matrix(i,k:length_matrix);
minimum=min_initial2;
for l=(k):(length_matrix)
    if (minimum==adjacency_matrix(i,l))
        pos=l;
        path_pos_minimum_initial2=[path_pos_minimum_initial2; i pos];
        path_temporary_minimum_initial2=[path_temporary_minimum_initial2 adjacency_matrix(i,l)];
    end
end
i=pos;
k=i+1;
j=k;
count=count+1; %So as to eliminate infinite looping in case that really happens.

while (j<=length_matrix & (count<iteration_value))% & till path_pos==node_final
    temporary_matrix=adjacency_matrix(i,k:length_matrix);
    minimum=min(temporary_matrix);
    for l=(k):(length_matrix)
        if (minimum==adjacency_matrix(i,l))
            pos=l;
            path_pos_minimum_initial2=[path_pos_minimum_initial2; i pos];
            path_temporary_minimum_initial2=[path_temporary_minimum_initial2 adjacency_matrix(i,l)];
        end
    end
    i=pos;
    k=i+1;
    j=k;
    count=count+1; %So as to eliminate infinite looping in case that really happens.
end
sum_initial2=sum(path_temporary_minimum_initial2);
end

if(length_matrix>3)
%THIRD MINIMUM FROM THE INITIAL
i=1;
j=2;
k=2;
pos=2;
count=1;
path_temporary_minimum_initial3=[];

path_pos_minimum_initial3=[];

temporary_matrix=adjacency_matrix(i,k:length_matrix);
minimum=min_initial3;
for l=(k):(length_matrix)
    if (minimum==adjacency_matrix(i,l))
        pos=l;
        path_pos_minimum_initial3=[path_pos_minimum_initial3; i pos];
        path_temporary_minimum_initial3=[path_temporary_minimum_initial3 adjacency_matrix(i,l)];
    end
end
i=pos;
k=i+1;
j=k;
count=count+1; %So as to eliminate infinite looping in case that really happens.

while (j<=length_matrix & (count<iteration_value))% & till path_pos==node_final
    temporary_matrix=adjacency_matrix(i,k:length_matrix);
    minimum=min(temporary_matrix);
    for l=(k):(length_matrix)
        if (minimum==adjacency_matrix(i,l))
            pos=l;
            path_pos_minimum_initial3=[path_pos_minimum_initial3; i pos];
            path_temporary_minimum_initial3=[path_temporary_minimum_initial3 adjacency_matrix(i,l)];
        end
    end
    i=pos;
    k=i+1;
    j=k;
    count=count+1; %So as to eliminate infinite looping in case that really happens.
end
sum_initial3=sum(path_temporary_minimum_initial3);
end
%quadcopter_matrix is the fliplr and flipud of adjacency_matrix to get the
%shortest paths from destination node also.

quadcopter_matrix=adjacency_matrix(length_matrix:-1:1,:);
quadcopter_matrix=quadcopter_matrix(:,length_matrix:-1:1);

if(length_matrix>1)
    
path_temporary_final=[];
path_pos_final=[];
i=1;
j=2;
k=2;
pos=2;
count=1;

%PRIMARY MINIMUM FROM THE FINAL

while (j<=length_matrix & (count<iteration_value))% & till path_pos==node_final
    temporary_matrix=quadcopter_matrix(i,k:length_matrix);
    minimum=min(temporary_matrix);
    for l=(k):(length_matrix)
        if (minimum==quadcopter_matrix(i,l))
            pos=l;
            path_pos_final=[path_pos_final; i pos];
            path_temporary_final=[path_temporary_final quadcopter_matrix(i,l)];
        end
    end
    i=pos;
    k=i+1;
    j=k;
    count=count+1; %So as to eliminate infinite looping in case that really happens.
end
sum_final=sum(path_temporary_final);
end

if(length_matrix>2)
    
%SECOND MINIMUM FROM THE FINAL
i=1;
j=2;
k=2;
pos=2;
count=1;
path_temporary_minimum_final2=[];

path_pos_minimum_final2=[];

temporary_matrix=quadcopter_matrix(i,k:length_matrix);
minimum=min_final2;
for l=(k):(length_matrix)
    if (minimum==quadcopter_matrix(i,l))
        pos=l;
        path_pos_minimum_final2=[path_pos_minimum_final2; i pos];
        path_temporary_minimum_final2=[path_temporary_minimum_final2 quadcopter_matrix(i,l)];
    end
end
i=pos;
k=i+1;
j=k;
count=count+1; %So as to eliminate infinite looping in case that really happens.

while (j<=length_matrix & (count<iteration_value))% & till path_pos==node_final
    temporary_matrix=quadcopter_matrix(i,k:length_matrix);
    minimum=min(temporary_matrix);
    for l=(k):(length_matrix)
        if (minimum==quadcopter_matrix(i,l))
            pos=l;
            path_pos_minimum_final2=[path_pos_minimum_final2; i pos];
            path_temporary_minimum_final2=[path_temporary_minimum_final2 quadcopter_matrix(i,l)];
        end
    end
    i=pos;
    k=i+1;
    j=k;
    count=count+1; %So as to eliminate infinite looping in case that really happens.
end
sum_final2=sum(path_temporary_minimum_final2);
end

if(length_matrix>3)
%THIRD MINIMUM FROM THE FINAL
i=1;
j=2;
k=2;
pos=2;
count=1;
path_temporary_minimum_final3=[];

path_pos_minimum_final3=[];

temporary_matrix=quadcopter_matrix(i,k:length_matrix);
minimum=min_initial3;
for l=(k):(length_matrix)
    if (minimum==quadcopter_matrix(i,l))
        pos=l;
        path_pos_minimum_final3=[path_pos_minimum_final3; i pos];
        path_temporary_minimum_final3=[path_temporary_minimum_final3 quadcopter_matrix(i,l)];
    end
end
i=pos;
k=i+1;
j=k;
count=count+1; %So as to eliminate infinite looping in case that really happens.

while (j<=length_matrix & (count<iteration_value))% & till path_pos==node_final
    temporary_matrix=quadcopter_matrix(i,k:length_matrix);
    minimum=min(temporary_matrix);
    for l=(k):(length_matrix)
        if (minimum==quadcopter_matrix(i,l))
            pos=l;
            path_pos_minimum_final3=[path_pos_minimum_final3; i pos];
            path_temporary_minimum_final3=[path_temporary_minimum_final3 quadcopter_matrix(i,l)];
        end
    end
    i=pos;
    k=i+1;
    j=k;
    count=count+1; %So as to eliminate infinite looping in case that really happens.
end
sum_final3=sum(path_temporary_minimum_final3);
end

orientation=pi/2;   %worst case initialisation

%if count==(iteration_value)
%   theta=((final_node_y_coordinate)-(initial_node_y_coordinate))/((final_node_x_coordinate)-(initial_node_x_coordinate));
%       while(orientation) %exit this loop if zero
%           orientation=abs(robot_orientation-theta);   %Better to blink
%       end                     %led until the detection of shortest path. 
%end

end