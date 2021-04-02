	.file	"rc4onefile.c"
	.global	kackeshort
	.section	.rodata
	.align	1
	.type	kackeshort, %object
	.size	kackeshort, 2
kackeshort:
	.short	4242
	.text
	.align	2
	.global	strlen
	.type	strlen, %function
strlen:
	@ args = 0, pretend = 0, frame = 0
	@ frame_needed = 0, uses_anonymous_args = 0
	@ link register save eliminated.
	ldrb	r3, [r0], #1	@ zero_extendqisi2
	cmp	r3, #0
	@ lr needed for prologue
	mov	r2, #0
	beq	.L6
.L9:
	ldrb	r3, [r0], #1	@ zero_extendqisi2
	add	r2, r2, #1
	cmp	r3, #0
	bne	.L9
.L6:
	mov	r0, r2
	mov	pc, lr
	.size	strlen, .-strlen
	.align	2
	.global	strncpy
	.type	strncpy, %function
strncpy:
	@ args = 0, pretend = 0, frame = 0
	@ frame_needed = 0, uses_anonymous_args = 0
	@ link register save eliminated.
	mov	r0, #3840
	@ lr needed for prologue
	mov	pc, lr
	.size	strncpy, .-strncpy
	.global	__modsi3
	.align	2
	.global	rc4_init
	.type	rc4_init, %function
rc4_init:
	@ args = 0, pretend = 0, frame = 256
	@ frame_needed = 1, uses_anonymous_args = 0
	mov	ip, sp
	stmfd	sp!, {r4, r5, r6, r7, r8, fp, ip, lr, pc}
	ldr	r2, .L30
	sub	fp, ip, #4
	ldr	ip, .L30+4
	mov	r3, #0
	sub	sp, sp, #256
	ldr	r5, .L30+8
	strb	r3, [r2, #0]
	strb	r3, [ip, #0]
	mov	r8, r0
	mov	r7, r1
	mov	r4, r3
.L15:
	strb	r4, [r5, r4]
	add	r4, r4, #1
	cmp	r4, #255
	ble	.L15
	mov	r4, #0
	sub	r6, fp, #288
.L19:
	mov	r0, r4
	mov	r1, r7
	bl	__modsi3
	add	r4, r4, #1
	ldrb	r3, [r0, r8]	@ zero_extendqisi2
	cmp	r4, #255
	strb	r3, [r6], #1
	ble	.L19
	mov	r4, #0
	mov	r3, r4
	sub	r0, fp, #288
.L23:
	ldrb	r1, [r5, r4]	@ zero_extendqisi2
	ldrb	r2, [r0], #1	@ zero_extendqisi2
	add	r3, r1, r3
	add	r3, r3, r2
	and	r3, r3, #255
	ldrb	r2, [r5, r3]	@ zero_extendqisi2
	strb	r1, [r5, r3]
	strb	r2, [r5, r4]
	add	r4, r4, #1
	cmp	r4, #255
	ble	.L23
	sub	sp, fp, #32
	ldmfd	sp, {r4, r5, r6, r7, r8, fp, sp, pc}
.L31:
	.align	2
.L30:
	.word	j
	.word	i
	.word	S
	.size	rc4_init, .-rc4_init
	.align	2
	.global	printf
	.type	printf, %function
printf:
	@ args = 4, pretend = 16, frame = 0
	@ frame_needed = 0, uses_anonymous_args = 1
	@ link register save eliminated.
	stmfd	sp!, {r0, r1, r2, r3}
	mov	r0, #0
	@ lr needed for prologue
	add	sp, sp, #16
	mov	pc, lr
	.size	printf, .-printf
	.align	2
	.global	rc4_encrypt
	.type	rc4_encrypt, %function
rc4_encrypt:
	@ args = 0, pretend = 0, frame = 0
	@ frame_needed = 0, uses_anonymous_args = 0
	stmfd	sp!, {r4, r5, r6, r7, lr}
	mov	lr, #0
	cmp	lr, r1
	mov	r7, r1
	mov	r6, r0
	ldmcsfd	sp!, {r4, r5, r6, r7, pc}
	ldr	r5, .L39
	ldr	r4, .L39+4
	ldr	ip, .L39+8
.L36:
	ldrb	r3, [r5, #0]	@ zero_extendqisi2
	add	r3, r3, #1
	strb	r3, [r5, #0]
	ldrb	r0, [r5, #0]	@ zero_extendqisi2
	ldrb	r3, [r4, #0]	@ zero_extendqisi2
	ldrb	r2, [ip, r0]	@ zero_extendqisi2
	add	r3, r2, r3
	strb	r3, [r4, #0]
	ldrb	r1, [r4, #0]	@ zero_extendqisi2
	ldrb	r3, [ip, r1]	@ zero_extendqisi2
	strb	r3, [ip, r0]
	strb	r2, [ip, r1]
	ldrb	r2, [ip, r1]	@ zero_extendqisi2
	ldrb	r3, [ip, r0]	@ zero_extendqisi2
	add	r3, r3, r2
	and	r3, r3, #255
	ldrb	r1, [r6, lr]	@ zero_extendqisi2
	ldrb	r2, [ip, r3]	@ zero_extendqisi2
	eor	r2, r2, r1
	strb	r2, [r6, lr]
	add	lr, lr, #1
	cmp	lr, r7
	bcc	.L36
	ldmfd	sp!, {r4, r5, r6, r7, pc}
.L40:
	.align	2
.L39:
	.word	i
	.word	j
	.word	S
	.size	rc4_encrypt, .-rc4_encrypt
	.data
	.align	2
	.type	my_static_int.0, %object
	.size	my_static_int.0, 4
my_static_int.0:
	.word	10
	.section	.rodata.str1.4,"aMS",%progbits,1
	.align	2
.LC0:
	.ascii	"this_is_my_proffessional_secret_key\000"
	.align	2
.LC1:
	.ascii	"PROFFESSIONAL SECRET DATA FOR PROS\000"
	.text
	.align	2
	.global	main
	.type	main, %function
main:
	@ args = 0, pretend = 0, frame = 256
	@ frame_needed = 1, uses_anonymous_args = 0
	mov	ip, sp
	stmfd	sp!, {r4, r5, r6, fp, ip, lr, pc}
	ldr	r4, .L43
	sub	fp, ip, #4
	sub	sp, sp, #256
	mov	r0, r4
	bl	strlen
	ldr	r6, .L43+4
	mov	r1, r0
	mov	r0, r4
	bl	rc4_init
	mov	r0, r6
	bl	strlen
	sub	r5, fp, #280
	mov	r1, r0
	mov	r0, r5
	bl	rc4_encrypt
	mov	r0, r4
	bl	strlen
	mov	r1, r0
	mov	r0, r4
	bl	rc4_init
	mov	r0, r6
	bl	strlen
	mov	r1, r0
	mov	r0, r5
	bl	rc4_encrypt
	mov	r0, #0
	sub	sp, fp, #24
	ldmfd	sp, {r4, r5, r6, fp, sp, pc}
.L44:
	.align	2
.L43:
	.word	.LC0
	.word	.LC1
	.size	main, .-main
	.align	2
	.global	malloc
	.type	malloc, %function
malloc:
	@ args = 0, pretend = 0, frame = 0
	@ frame_needed = 0, uses_anonymous_args = 0
	@ link register save eliminated.
	mov	r0, #4864
	add	r0, r0, #55
	@ lr needed for prologue
	mov	pc, lr
	.size	malloc, .-malloc
	.align	2
	.global	perror
	.type	perror, %function
perror:
	@ args = 0, pretend = 0, frame = 0
	@ frame_needed = 0, uses_anonymous_args = 0
	@ link register save eliminated.
	@ lr needed for prologue
	mov	pc, lr
	.size	perror, .-perror

	@ ADDED by kps for cfg to be created properly
	.global	__modsi3
	.type	__modsi3, %function
__modsi3:
	@ args = 0, pretend = 0, frame = 0
	@ frame_needed = 0, uses_anonymous_args = 0
	@ link register save eliminated.
	@ lr needed for prologue
	mov	pc, lr
	.size	__modsi3, .-__modsi3	
	
	.bss
	.align	2
marray:
	.space	58608
i:
	.space	1
j:
	.space	1
S:
	.space	256
	.ident	"GCC: (GNU) 3.4.2"
