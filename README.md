RegexReplacer
=============
(中文介绍请看这：http://www.cnblogs.com/trytocatch/p/RegexReplacer.html)

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
<b>$function name[alias](arg1,arg2,arg3)</b>(the 'alias' is related to function 'Ref' and 'StcRef')<br>
<br>
If you want to write special character, like '$', you should put a escape character '\' ahead of it. <br>
If you write nothing in the position of a parameter, it will get an empty string rather than 'null'<br>
<br>
The functions in replace expression will be called in order from left to right, you should notice this while you want to use the function 'Ref' or 'StcRef'.<br>
If function A is the parameter of function B, then A will be called before B be called(but the function 'Iif' is an exception)<br>
<br>
<b>Notices: </b><br>
1. For the functions:Add, Subtract, Multiply, Divide, Mod, Seq, if there is a decimal in their parameters, the result will be a decimal too, otherwise returns a integer(in fact, it's a 'Long'), even for Divide. Empty string and 'null' will be treated as 0, '1.0' and '1.' will be treated as decimal<br>
<br>
2. If the function 'Seq' be used, and replaced part of the matched content only, the actual replacement may be different from the replacement displayed in the result table(because it is sequence, it depends on the number of replacement)<br>
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
<h2>Cases:</h2>
<h3>1. Contribute to log analysis</h3>
<b>Requirement: </b>In general, the log contains various informations, sometimes you just need one type of them, and it's difficult to visualize the interested content even with the UE, you may have to view the items one by one with the 'next' button.<br>
<b>Solution: </b>Copy the log to the content box, input a regular expression to match the interested content, the result table will display them. Then check the 'return focus' and click one result, the cursor will be located to the right place in content box, you can conveniently get the context. Or you can just pick out all the interested content(put '$(0)' in replace expression box and check 'replacement only', then click the button 'replace all')<br>
<h3>2. capturing group and arithmetic</h3>
<b>Original content:</b><br>
3*4=?<br>
-6*12=?<br>
9*-5=?<br>
<b>Requirement: </b>convert to:<br>
3*4=12<br>
-6*12=-72<br>
9*-5=-45<br>
<b>Solution:</b><br>
<b>regular expression:</b><br>
<pre>(-?\d+)\*(-?\d+)=\?</pre>
<b>replace expression:</b><br>
<pre>$(1)*$(2)=$*($(1),$(2))</pre>
<h3>3. Sequence</h3>
<b>Original content:</b><br>
a=34<br>
b=65<br>
c=54<br>
<b>Requirement: </b>add a sequence number for each line, start with 10, and increment is 10<br>
10. a=34<br>
20. b=65<br>
30. c=54<br>
<b>Solution:</b><br>
check the regex flag: 'MULTILINE'<br>
<b>regular expression:</b><br>
<pre>^</pre>
<b>replace expression:</b><br>
<pre>$Seq(10,10). </pre>
<br>
<h3>4. Sequence 2(this case is complex and meaningless.Just to show that how complex things it could generate)</h3>
<b>Requirement: </b>make a multiplication table<br>
<pre>
1 * 1 = 1
1 * 2 = 2	2 * 2 = 4
1 * 3 = 3	2 * 3 = 6	3 * 3 = 9
...
</pre>
<b>Solution: </b>write 45 characters as you like in the content box<br>
<b>regular expression:</b><br>
<pre>.</pre>
<b>replace expression:</b><br>
<pre>$Iif($Seq[n](1,1,$Seq[m](1,1,9)),1,,	)$StcRef(n) * $StcRef(m) = $*($StcRef(n),$StcRef(m))$Iif($StcRef(n),$StcRef(m),
,)

</pre>
<h3>5. Case conversion</h3>
<b>Original content: </b>a snippet from a document<br>
permission types: typea,typeb,typec,typed<br>
<b>Requirement: </b>generate the Java source codes<br>
public static final byte TYPEA = 1;<br>
public static final byte TYPEB = 2;<br>
public static final byte TYPEC = 4;<br>
public static final byte TYPED = 8;<br>
<b>Solution:</b><br>
<b>regular expression:</b><br>
<pre>\w+<br></pre>
<b>replace expression:</b><br>
<pre>public static final byte $Upper($(0)) = $Iif[tv]($*[v]($StcRef(tv),2),0,1,$StcRef(v));

</pre>
check the check box 'replacement only', then click the button 'replace all'<br>
<br>
<h3>6. Replace strings to assigned strings(complex)</h3>
<b>Original content: </b>(extract from an examination paper)<br>
Miss Carter is a beautiful girl. Her father __ two years ago and her mother made a terrible mistake and __. They began to live a hard life. When she __ middle school, she couldn't go on studying. Her uncle found a __ for her...<br>
<b>Requirement: </b>put the answers to the right place<br>
answers:<br>
1. died<br>
2. left<br>
3. finished<br>
4. job<br>
...<br>
<b>Solution:</b><br>
put the content into content box<br>
input a <b>regular expression</b>:<br>
<pre>__</pre>
input a <b>replace expression</b>:<br>
<pre>\$($Seq(1,1))</pre>
click 'replace all', get the result(marked as 'StrA'): <br>
Miss Carter is a beautiful girl. Her father $(1) two years ago and her mother made a terrible mistake and $(2). They began to live a hard life. When she $(3) middle school, she couldn't go on studying. Her uncle found a $(4) for her...<br>

<br>
then put the answers into content box<br>
input a <b>regular expression</b>:<br>
<pre>\d+\. (\w+)</pre>
input a <b>replace expression</b>:<br>
<pre>$(1)</pre>
check 'replacement only' then click 'replace all', get the result(marked as 'StrB'):<br>
diedleftfinishedjob<br>
change the <b>replace expression</b> to: <br>
<pre>($(0))</pre>
check 'replacement only' then click 'replace all', get the result(marked as 'StrC'):<br>
(died)(left)(finished)(job)<br>
<br>
Now put the StrB into content box<br>
and put the StrC into <b>regular expression</b> box<br>
and put the StrA into <b>replace expression</b> box<br>
then click 'replace all', you'll get the final result:<br>
Miss Carter is a beautiful girl. Her father died two years ago and her mother made a terrible mistake and left. They began to live a hard life. When she finished middle school, she couldn't go on studying. Her uncle found a job for her...<br>
<br>
