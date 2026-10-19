package com.mattz.anim.animations

import com.mattz.anim.animations.bounce.BounceEnlargeAnimator
import com.mattz.anim.animations.bounce.BounceInDownAnimator
import com.mattz.anim.animations.bounce.BounceInLeftAnimator
import com.mattz.anim.animations.bounce.BounceInRightAnimator
import com.mattz.anim.animations.bounce.BounceInUpAnimator
import com.mattz.anim.animations.bounce.BounceShrinkAnimator
import com.mattz.anim.animations.fade.FadeInAnimator
import com.mattz.anim.animations.fade.FadeInDownAnimator
import com.mattz.anim.animations.fade.FadeInLeftAnimator
import com.mattz.anim.animations.fade.FadeInRightAnimator
import com.mattz.anim.animations.fade.FadeInUpAnimator
import com.mattz.anim.animations.fade.FadeOutAnimator
import com.mattz.anim.animations.fade.FadeOutDownAnimator
import com.mattz.anim.animations.fade.FadeOutLeftAnimator
import com.mattz.anim.animations.fade.FadeOutRightAnimator
import com.mattz.anim.animations.fade.FadeOutUpAnimator
import com.mattz.anim.animations.other.PulseAnimator
import com.mattz.anim.animations.other.ShakeAnimator
import com.mattz.anim.animations.other.WobbleAnimator
import com.mattz.anim.animations.slide.SlideInDownAnimator
import com.mattz.anim.animations.slide.SlideInLeftAnimator
import com.mattz.anim.animations.slide.SlideInRightAnimator
import com.mattz.anim.animations.slide.SlideInUpAnimator
import com.mattz.anim.animations.slide.SlideOutDownAnimator
import com.mattz.anim.animations.slide.SlideOutLeftAnimator
import com.mattz.anim.animations.slide.SlideOutRightAnimator
import com.mattz.anim.animations.slide.SlideOutUpAnimator

enum class Animations(val animator: BaseAnimator) {
    //Bounce
    BounceInDown(BounceInDownAnimator()),
    BounceInLeft(BounceInLeftAnimator()),
    BounceInRight(BounceInRightAnimator()),
    BounceInUp(BounceInUpAnimator()),
    BounceEnlarge(BounceEnlargeAnimator()),
    BounceShrink(BounceShrinkAnimator()),

    //Fade in
    FadeIn(FadeInAnimator()),
    FadeInLeft(FadeInLeftAnimator()),
    FadeInRight(FadeInRightAnimator()),
    FadeInUp(FadeInUpAnimator()),
    FadeInDown(FadeInDownAnimator()),

    //Fade out
    FadeOut(FadeOutAnimator()),
    FadeOutLeft(FadeOutLeftAnimator()),
    FadeOutRight(FadeOutRightAnimator()),
    FadeOutUp(FadeOutUpAnimator()),
    FadeOutDown(FadeOutDownAnimator()),

    //Slide in
    SlideInLeft(SlideInLeftAnimator()),
    SlideInRight(SlideInRightAnimator()),
    SlideInUp(SlideInUpAnimator()),
    SlideInDown(SlideInDownAnimator()),

    //Slide out
    SlideOutLeft(SlideOutLeftAnimator()),
    SlideOutRight(SlideOutRightAnimator()),
    SlideOutUp(SlideOutUpAnimator()),
    SlideOutDown(SlideOutDownAnimator()),

    //Other
    Pulse(PulseAnimator()),
    Wobble(WobbleAnimator()),
    Shake(ShakeAnimator())
}