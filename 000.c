#include<stdio.h>
main()
{
	char a,b;
	scanf("%c%c",&a,&b);
	if((a>=97&&a<=122)&&(b>=97&&b<=122))       //if(a>=65&&a<=90)orif(a>='A'&&a<='Z')
		printf("YES\n");
	else
	    printf("NO\n");
}