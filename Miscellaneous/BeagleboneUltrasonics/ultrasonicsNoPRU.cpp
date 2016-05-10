/*
    This file is part of TraQuad-project's software, version Alpha (unstable release).

    TraQuad-project's software is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    TraQuad-project's software is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with TraQuad-project's software.  If not, see <http://www.gnu.org/licenses/>.

    Additional term: Clause 7(b) of GPLv3. Attribution is (even more) necessary if these (TraQuad-project's) softwares are distributed commercially.
    Date of creation: June 2015 - June 2016 and Attribution: Prasad N R as a representative of (unregistered) company TraQuad.
 */

#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <cstdlib>
#include <iostream>
#include <unistd.h>
#include <sys/wait.h> 
#include <sys/time.h>
#include "libsoc_gpio.h"
#include "libsoc_debug.h"
 
#define GPIO_OUTPUT 50
#define GPIO_INPUT 30

using namespace std;
gpio *gpio_output, *gpio_input;

int main(void) { 
libsoc_set_debug(0);

int i = 0;
long int aImax = 3, value = 0, sum = 0, aI = 0;

gpio_output = libsoc_gpio_request(GPIO_OUTPUT, LS_SHARED);
gpio_input = libsoc_gpio_request(GPIO_INPUT, LS_SHARED); 
libsoc_gpio_set_direction(gpio_input, INPUT); 
libsoc_gpio_set_direction(gpio_output, OUTPUT);

while(1){
aI = 0;
	while(aI<aImax)
	{
		sum = 0;
		i = 0;
		libsoc_gpio_set_level (gpio_output , HIGH );
		usleep(10);
		libsoc_gpio_set_level ( gpio_output , LOW );
		
		while(1){
			if(libsoc_gpio_get_level(gpio_input)==HIGH){
	        	        i++;}
			if(libsoc_gpio_get_level(gpio_input)==LOW){
				break;}
		}
		sum = sum + i; 
		usleep(1000);
		aI++;
	}
	value = sum/aImax;
	if(value > 0)
	{cout << value << endl;}
}
return 0;
}
