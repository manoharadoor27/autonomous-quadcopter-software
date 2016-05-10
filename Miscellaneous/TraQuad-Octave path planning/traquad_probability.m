function weight_return=traquad_probability(node_initial_x,node_initial_y,node_final_x,node_final_y)
global part_matrix;
global mapreproduced
global roadmap
global map_quadcopter
global weight_return
global node_initial_x
global node_initial_y
global node_final_x
global node_final_y
%Parameter rough_smooth determines the exactitude of the time-distance
%calibration which is currently working with a range 0-50%.
flag=0;
normal0=[0.50000];
normal1=[0.46020 0.42070 0.38209 0.34458 0.30854 0.27425 0.24196 0.21186 0.18406 0.15866];
normal2=[0.13567 0.11507 0.09680 0.08076 0.06681 0.05480 0.04457 0.03593 0.02872 0.02275];
normal3=[0.01786 0.01390 0.01072 0.00820 0.00621 0.00466 0.00347 0.00256 0.00187 0.00135];

normal_table=[normal0 normal1 normal2 normal3];

normal_threshold=2; %Reducing the scale by normal_threshold times.

len_normal=length(normal_table);

for i=1:(len_normal/normal_threshold)
    normal(i)=normal_table(i*normal_threshold);
end

clear len_normal;
%Stage 1: Getting the coordinates of the straight line.

%0-Unexplored 1-Potholes 2-Rough roads 3-Smooth roads

%node_initial and node_final are the parameters to be received by this
%function.

rough_smooth=0.367 %Parameter rough_smooth determines the exactitude of the time-distance
%calibration which is currently working with a range 0-50%.

[node_initial_x node_final_x node_initial_y node_final_y];
%For this project, for demonstration, it is better to assign half-weight to
%rough roads and set the accelerometer threshold accordingly.
%This code is meant for 2 nodes only initial and final as the map would
%have been scaled to obtain initial and final points at the corners.
%node=[x y] format

%node_initial and node_final are the parameters to be received by this
%function.
virtual_infinity=10000;
node=[node_initial_x node_initial_y; node_final_x node_final_y];

%Please note that, modulus has to be taken care of by specifying
%conditions.
%Also note that, this code is bidirectional. Because, all that is needed
%out of this code is weight.

lenx=node_initial_x-node_final_x;
leny=node_initial_y-node_final_y;

lenx=abs(lenx);%Length is positive
leny=abs(leny);%Length is positive

if ((node_initial_x<node_final_x)&(node_initial_y<node_final_y))
    part_matrix=map_quadcopter(node_initial_x:node_final_x,node_initial_y:node_final_y);
end

if ((node_initial_x>node_final_x)&(node_initial_y<node_final_y))
    part_matrix=map_quadcopter(node_final_x:node_initial_x,node_initial_y:node_final_y);
end

if ((node_initial_x<node_final_x)&(node_initial_y>node_final_y))
    part_matrix=map_quadcopter(node_initial_x:node_final_x,node_final_y:node_initial_y);
end

if ((node_initial_x>node_final_x)&(node_initial_y>node_final_y))
    part_matrix=map_quadcopter(node_final_x:node_initial_x,node_final_y:node_initial_y);
end

map=part_matrix;%Part of the real map between the nodes.

if node_initial_x >= node_final_x
    xmin=node_final_x;
    xmax=node_initial_x;
end

if node_initial_x <= node_final_x
    xmin=node_initial_x;
    xmax=node_final_x;
end

if node_initial_y >= node_final_y
    ymin=node_final_y;
    ymax=node_initial_y;
end

if node_initial_y <= node_final_y
    ymin=node_initial_y;
    ymax=node_final_y;
end
road=[];
x=[];
y=[];

 loop = max(lenx,leny);

if node_initial_x <= node_final_x
    if node_initial_y <= node_final_y
        for m=1:loop
            coordinatey=round(xmin+m*lenx/loop); % Need not be round necessarily. Integer would be fine.
            y=[y coordinatey];
            coordinatex=round(ymin+m*leny/loop); % Need not be round necessarily. Integer would be fine.
            x=[x coordinatex];
        end
        road=[x;y]'; %Straight line is thus obtained. Stage 1 is finished.
    end
end

if node_initial_x <= node_final_x
    if node_initial_y > node_final_y
        for m=1:loop
            coordinatey=round(xmin+m*lenx/loop); % Need not be round necessarily. Integer would be fine.
            y=[y coordinatey];
            coordinatex=round(ymin-m*leny/loop); % Need not be round necessarily. Integer would be fine.
            x=[x coordinatex];
        end
        road=[x;y]';
        if(isempty(road)==0)
        road=[road(:,2) loop+road(:,1)]
        end
    end
end

%Same cases as these are bidirectional.
if node_initial_x >= node_final_x
    if node_initial_y <= node_final_y
        for m=1:loop
            coordinatey=round(xmin+m*lenx/loop); % Need not be round necessarily. Integer would be fine.
            y=[y coordinatey];
            coordinatex=round(ymin-m*leny/loop); % Need not be round necessarily. Integer would be fine.
            x=[x coordinatex];
        end
        road=[x;y]';
        if(isempty(road)==0)
        road=[road(:,2) loop+road(:,1)];
        end
    end
end

if node_initial_x >= node_final_x
    if node_initial_y >= node_final_y
        for m=1:loop
            coordinatey=round(xmin+m*lenx/loop); % Need not be round necessarily. Integer would be fine.
            y=[y coordinatey];
            coordinatex=round(ymin+m*leny/loop); % Need not be round necessarily. Integer would be fine.
            x=[x coordinatex];
        end
        road=[x;y]'; %Straight line is thus obtained. Stage 1 is finished.
    end
end

roadDiagonalValue=10; %(Any value which is distinct (used only for computations) is fine(excepting the parameter values 0,1,2,3))
roadmap=[];
if(isempty(roadmap)==0)
for k=1:length(road(:,1))
    roadmap(road(k,1),road(k,2))=roadDiagonalValue;
end
end

%Stage 2: Getting the distances of the dirty potholes and the road with
%respect to x coordinates and y coordinates using roadmap.

%Good thing is that even stage 3 has been covered in this stage.

xsum=0;
ysum=0;
pos=0;

for i=1:lenx
    for j=1:leny
        if(isempty(roadmap)==0)
        if(roadmap(j,i)==roadDiagonalValue)
            pos=j;
        end
        end
    end
    for j=1:leny
        if(map(i,j)==2)%Rough roads
            ydistance=round(abs(j-pos));%Integer would be fine. round is not necessary.
            ysum=ysum+rough_smooth*normal(ydistance+1);
        end
        if(map(i,j)==3)%Smooth roads
            ydistance=round(abs(j-pos));%Integer would be fine. round is not necessary.
            ysum=ysum+normal(ydistance+1)
        end
    end
end

for j=1:leny
    for i=1:lenx
        if(isempty(roadmap)==0)
        if(roadmap(j,i)==roadDiagonalValue)
            pos=i;
        end
        end
    end
    for i=1:lenx
        if(map(i,j)==2)%Rough roads
            xdistance=round(abs(i-pos));%Integer would be fine. round is not necessary.
            xsum=xsum+rough_smooth*normal(xdistance+1);
        end
        if(map(i,j)==3)%Smooth roads
            xdistance=round(abs(i-pos));%Integer would be fine. round is not necessary.
            xsum=xsum+normal(xdistance+1);
        end
    end
end

xproduct=1;
yproduct=1;
%Potholes' probabilities.
for i=1:lenx
    for j=1:leny
        if(isempty(roadmap)==0)
        if(roadmap(j,i)==roadDiagonalValue)
            pos=j;
        end
        end
    end
    for j=1:leny
        if(map(i,j)==1)
            ydistance=round(abs(j-pos));%Integer would be fine. round is not necessary.
            yproduct=yproduct*(1-normal(ydistance+1));
        end
    end
end

for j=1:leny
    for i=1:lenx
        if(isempty(roadmap)==0)
        if(roadmap(j,i)==roadDiagonalValue)
            pos=i;
        end
        end
    end
    for i=1:lenx
        if(map(i,j)==1)
            xdistance=round(abs(i-pos));
            xproduct=xproduct*(1-normal(xdistance+1));
        end
    end
end

weight_sum=1.414*sqrt(xsum*xsum+ysum*ysum); %Because, normal distribution is meant for 0.5 stuffs.
weight_product=xproduct*yproduct;

weight=weight_sum*weight_product;

if node_initial_x==node_final_x
    weight=0;
    for i=1:length(map)
        if map(i)==3
            weight=weight+1;
        end
        if map(i)==2
            weight=weight+rough_smooth;
        end
        if map(i)==1
            flag=1;
        end
    end
    if flag==1
        weight=0;
    end
end

if node_initial_y==node_final_y
    weight=0;
    for i=1:length(map)
        if map(i)==3
            weight=weight+1;
        end
        if map(i)==2
            weight=weight+rough_smooth;
        end
        if map(i)==1
            flag=1;
        end
    end
    if flag==1
        weight=0;
    end
end

if weight==0
    weight_return=virtual_infinity;
else
    weight_return=(node_final_x-node_initial_x)*(node_final_y-node_initial_y)/weight;
end

if weight_return>virtual_infinity
    weight_return=virtual_infinity;
end

end