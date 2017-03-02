`timescale 1ns/10ps

module sram8t25x64 ( A1, CE1, OEB1, CSB1, O1, A2, CE2, WEB2, CSB2, I2 );
input CE1;
input OEB1;
input CSB1;
input [4:0] A1;
output reg [63:0] O1;
input CE2;
input WEB2;
input CSB2;
input [4:0] A2;
input [63:0] I2;

reg notifier;

specify
$setuphold(posedge CE1, OEB1, 0, 0, notifier);
$setuphold(posedge CE1, CSB1, 0, 0, notifier);
$setuphold(posedge CE1, A1[0], 0, 0, notifier);
$setuphold(posedge CE1, A1[1], 0, 0, notifier);
$setuphold(posedge CE1, A1[2], 0, 0, notifier);
$setuphold(posedge CE1, A1[3], 0, 0, notifier);
$setuphold(posedge CE1, A1[4], 0, 0, notifier);
(posedge CE1 => O1[0]) = (0.3:0.3:0.3);
(posedge CE1 => O1[1]) = (0.3:0.3:0.3);
(posedge CE1 => O1[2]) = (0.3:0.3:0.3);
(posedge CE1 => O1[3]) = (0.3:0.3:0.3);
(posedge CE1 => O1[4]) = (0.3:0.3:0.3);
(posedge CE1 => O1[5]) = (0.3:0.3:0.3);
(posedge CE1 => O1[6]) = (0.3:0.3:0.3);
(posedge CE1 => O1[7]) = (0.3:0.3:0.3);
(posedge CE1 => O1[8]) = (0.3:0.3:0.3);
(posedge CE1 => O1[9]) = (0.3:0.3:0.3);
(posedge CE1 => O1[10]) = (0.3:0.3:0.3);
(posedge CE1 => O1[11]) = (0.3:0.3:0.3);
(posedge CE1 => O1[12]) = (0.3:0.3:0.3);
(posedge CE1 => O1[13]) = (0.3:0.3:0.3);
(posedge CE1 => O1[14]) = (0.3:0.3:0.3);
(posedge CE1 => O1[15]) = (0.3:0.3:0.3);
(posedge CE1 => O1[16]) = (0.3:0.3:0.3);
(posedge CE1 => O1[17]) = (0.3:0.3:0.3);
(posedge CE1 => O1[18]) = (0.3:0.3:0.3);
(posedge CE1 => O1[19]) = (0.3:0.3:0.3);
(posedge CE1 => O1[20]) = (0.3:0.3:0.3);
(posedge CE1 => O1[21]) = (0.3:0.3:0.3);
(posedge CE1 => O1[22]) = (0.3:0.3:0.3);
(posedge CE1 => O1[23]) = (0.3:0.3:0.3);
(posedge CE1 => O1[24]) = (0.3:0.3:0.3);
(posedge CE1 => O1[25]) = (0.3:0.3:0.3);
(posedge CE1 => O1[26]) = (0.3:0.3:0.3);
(posedge CE1 => O1[27]) = (0.3:0.3:0.3);
(posedge CE1 => O1[28]) = (0.3:0.3:0.3);
(posedge CE1 => O1[29]) = (0.3:0.3:0.3);
(posedge CE1 => O1[30]) = (0.3:0.3:0.3);
(posedge CE1 => O1[31]) = (0.3:0.3:0.3);
(posedge CE1 => O1[32]) = (0.3:0.3:0.3);
(posedge CE1 => O1[33]) = (0.3:0.3:0.3);
(posedge CE1 => O1[34]) = (0.3:0.3:0.3);
(posedge CE1 => O1[35]) = (0.3:0.3:0.3);
(posedge CE1 => O1[36]) = (0.3:0.3:0.3);
(posedge CE1 => O1[37]) = (0.3:0.3:0.3);
(posedge CE1 => O1[38]) = (0.3:0.3:0.3);
(posedge CE1 => O1[39]) = (0.3:0.3:0.3);
(posedge CE1 => O1[40]) = (0.3:0.3:0.3);
(posedge CE1 => O1[41]) = (0.3:0.3:0.3);
(posedge CE1 => O1[42]) = (0.3:0.3:0.3);
(posedge CE1 => O1[43]) = (0.3:0.3:0.3);
(posedge CE1 => O1[44]) = (0.3:0.3:0.3);
(posedge CE1 => O1[45]) = (0.3:0.3:0.3);
(posedge CE1 => O1[46]) = (0.3:0.3:0.3);
(posedge CE1 => O1[47]) = (0.3:0.3:0.3);
(posedge CE1 => O1[48]) = (0.3:0.3:0.3);
(posedge CE1 => O1[49]) = (0.3:0.3:0.3);
(posedge CE1 => O1[50]) = (0.3:0.3:0.3);
(posedge CE1 => O1[51]) = (0.3:0.3:0.3);
(posedge CE1 => O1[52]) = (0.3:0.3:0.3);
(posedge CE1 => O1[53]) = (0.3:0.3:0.3);
(posedge CE1 => O1[54]) = (0.3:0.3:0.3);
(posedge CE1 => O1[55]) = (0.3:0.3:0.3);
(posedge CE1 => O1[56]) = (0.3:0.3:0.3);
(posedge CE1 => O1[57]) = (0.3:0.3:0.3);
(posedge CE1 => O1[58]) = (0.3:0.3:0.3);
(posedge CE1 => O1[59]) = (0.3:0.3:0.3);
(posedge CE1 => O1[60]) = (0.3:0.3:0.3);
(posedge CE1 => O1[61]) = (0.3:0.3:0.3);
(posedge CE1 => O1[62]) = (0.3:0.3:0.3);
(posedge CE1 => O1[63]) = (0.3:0.3:0.3);
$setuphold(posedge CE2, WEB2, 0, 0, notifier);
$setuphold(posedge CE2, CSB2, 0, 0, notifier);
$setuphold(posedge CE2, A2[0], 0, 0, notifier);
$setuphold(posedge CE2, A2[1], 0, 0, notifier);
$setuphold(posedge CE2, A2[2], 0, 0, notifier);
$setuphold(posedge CE2, A2[3], 0, 0, notifier);
$setuphold(posedge CE2, A2[4], 0, 0, notifier);
$setuphold(posedge CE2, I2[0], 0, 0, notifier);
$setuphold(posedge CE2, I2[1], 0, 0, notifier);
$setuphold(posedge CE2, I2[2], 0, 0, notifier);
$setuphold(posedge CE2, I2[3], 0, 0, notifier);
$setuphold(posedge CE2, I2[4], 0, 0, notifier);
$setuphold(posedge CE2, I2[5], 0, 0, notifier);
$setuphold(posedge CE2, I2[6], 0, 0, notifier);
$setuphold(posedge CE2, I2[7], 0, 0, notifier);
$setuphold(posedge CE2, I2[8], 0, 0, notifier);
$setuphold(posedge CE2, I2[9], 0, 0, notifier);
$setuphold(posedge CE2, I2[10], 0, 0, notifier);
$setuphold(posedge CE2, I2[11], 0, 0, notifier);
$setuphold(posedge CE2, I2[12], 0, 0, notifier);
$setuphold(posedge CE2, I2[13], 0, 0, notifier);
$setuphold(posedge CE2, I2[14], 0, 0, notifier);
$setuphold(posedge CE2, I2[15], 0, 0, notifier);
$setuphold(posedge CE2, I2[16], 0, 0, notifier);
$setuphold(posedge CE2, I2[17], 0, 0, notifier);
$setuphold(posedge CE2, I2[18], 0, 0, notifier);
$setuphold(posedge CE2, I2[19], 0, 0, notifier);
$setuphold(posedge CE2, I2[20], 0, 0, notifier);
$setuphold(posedge CE2, I2[21], 0, 0, notifier);
$setuphold(posedge CE2, I2[22], 0, 0, notifier);
$setuphold(posedge CE2, I2[23], 0, 0, notifier);
$setuphold(posedge CE2, I2[24], 0, 0, notifier);
$setuphold(posedge CE2, I2[25], 0, 0, notifier);
$setuphold(posedge CE2, I2[26], 0, 0, notifier);
$setuphold(posedge CE2, I2[27], 0, 0, notifier);
$setuphold(posedge CE2, I2[28], 0, 0, notifier);
$setuphold(posedge CE2, I2[29], 0, 0, notifier);
$setuphold(posedge CE2, I2[30], 0, 0, notifier);
$setuphold(posedge CE2, I2[31], 0, 0, notifier);
$setuphold(posedge CE2, I2[32], 0, 0, notifier);
$setuphold(posedge CE2, I2[33], 0, 0, notifier);
$setuphold(posedge CE2, I2[34], 0, 0, notifier);
$setuphold(posedge CE2, I2[35], 0, 0, notifier);
$setuphold(posedge CE2, I2[36], 0, 0, notifier);
$setuphold(posedge CE2, I2[37], 0, 0, notifier);
$setuphold(posedge CE2, I2[38], 0, 0, notifier);
$setuphold(posedge CE2, I2[39], 0, 0, notifier);
$setuphold(posedge CE2, I2[40], 0, 0, notifier);
$setuphold(posedge CE2, I2[41], 0, 0, notifier);
$setuphold(posedge CE2, I2[42], 0, 0, notifier);
$setuphold(posedge CE2, I2[43], 0, 0, notifier);
$setuphold(posedge CE2, I2[44], 0, 0, notifier);
$setuphold(posedge CE2, I2[45], 0, 0, notifier);
$setuphold(posedge CE2, I2[46], 0, 0, notifier);
$setuphold(posedge CE2, I2[47], 0, 0, notifier);
$setuphold(posedge CE2, I2[48], 0, 0, notifier);
$setuphold(posedge CE2, I2[49], 0, 0, notifier);
$setuphold(posedge CE2, I2[50], 0, 0, notifier);
$setuphold(posedge CE2, I2[51], 0, 0, notifier);
$setuphold(posedge CE2, I2[52], 0, 0, notifier);
$setuphold(posedge CE2, I2[53], 0, 0, notifier);
$setuphold(posedge CE2, I2[54], 0, 0, notifier);
$setuphold(posedge CE2, I2[55], 0, 0, notifier);
$setuphold(posedge CE2, I2[56], 0, 0, notifier);
$setuphold(posedge CE2, I2[57], 0, 0, notifier);
$setuphold(posedge CE2, I2[58], 0, 0, notifier);
$setuphold(posedge CE2, I2[59], 0, 0, notifier);
$setuphold(posedge CE2, I2[60], 0, 0, notifier);
$setuphold(posedge CE2, I2[61], 0, 0, notifier);
$setuphold(posedge CE2, I2[62], 0, 0, notifier);
$setuphold(posedge CE2, I2[63], 0, 0, notifier);
endspecify

reg [63:0] memory[24:0];
`ifndef SYNTHESIS
  integer initvar;
  initial begin
    #0.002;
    for (initvar = 0; initvar < 25; initvar = initvar+1)
      memory[initvar] = {2 {$random}};
  end
`endif
always @ (posedge CE1)
  if (~CSB1)
    O1 <= memory[A1];

always @ (posedge CE2)
begin
  if (~CSB2 & ~WEB2)
  begin
    memory[A2] <= I2;
  end
end

endmodule
