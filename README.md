RegexReplacer
=============
(中文介绍请看这：http://www.cnblogs.com/trytocatch/p/RegexReplacer.html)

You can get the executable jar from [here](https://github.com/trytocatch/RegexReplacer/raw/master/executable%20jar/RegexReplacer.jar), and of course, you need a jre to execute it.

<h2>Major functions:</h2>
Make complex replacements with regular expression<br>
<h2>Characteristic:</h2>
1. It introduced replace functions, you can make some complex replacements with it, if it doesn't meet your needs, you can also write your own function(refer to 'custom function guidance')<br>
2. Shows results in real time, you can pick some of them to make replacements, or just view what's matched<br>
3. You can just take the matched content(sometimes it's very useful)<br>
<br>
<h2>Replace expression</h2>
In the replace expression, you can write replace function inside the plain string(they will be concatenated into a new string)<br>
The form of function is:<br>
<b>$function name(arg1,arg2,arg3)</b><br>
<b>$function name\[alias\](arg1,arg2,arg3)</b>(the 'alias' is related to function 'Ref' and 'StcRef')<br>
<br>
If you want to write special character, like '$', you should put a escape character '\' ahead of it. <br>
If you write nothing in the position of a parameter, it will get an empty string rather than 'null'<br>
<br>
The functions in replace expression will be called in order from left to right, you should notice this while you want to use the function 'Ref' or 'StcRef'.<br>
If function A is the parameter of function B, then A will be called before B be called(but the function 'Iif' is an exception)<br>
<br>
<b>Notices: </b><br>
 For the functions:Add, Subtract, Multiply, Divide, Mod, Seq, if there is a decimal in their parameters, the result will be a decimal too, otherwise returns a integer(in fact, it's a 'Long'), even for Divide. Empty string and 'null' will be treated as 0, '1.0' and '1.' will be treated as decimal<br>
<br>
 If the function 'Seq' be used, and replaced part of the matched content only, the actual replacement may be different from the replacement displayed in the result table(because it is sequence, it depends on the number of replacement)<br>
<br>
Example 1:"No $Seq(1,1):"<br>
It will generates:<br>
No 1:<br>
No 2:<br>
No 3:<br>
...<br>
Example 2:"$Iif($AbsRow(),1,List,No $Seq(1,1):)"<br>
It will generates:<br>
List<br>
No 1:<br>
No 2:<br>
No 3:<br>
...<br>
<br>

Markdown has driven me crazy, you can get the help from '/resource/htmls/Help.html'.
